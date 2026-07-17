package com.example.waterreminder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.waterreminder.Room.AppDatabase
import com.example.waterreminder.Room.LocalRecordEntity
import com.example.waterreminder.Room.LocalUserEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

enum class Gender { Male, Female }


data class UiRecord(
    val amount: Int,
    val waterType: String,
    val timestamp: Long
)

data class UiState(
    val name: String = "",
    val gender: Gender? = null,
    val drinkingGoals: Int = 0,
    val drinkingCount: Int = 0,
    val drinkingRecords: Int = 0,
    val waterType: String = "",
    val recordList: MutableList<UiRecord> = mutableListOf(),
    val currentUserKey: String = "",
    val recordIds: MutableList<String> = mutableListOf()
) {
    val isWelcomePageNextEnabled: Boolean
        get() = name.isNotBlank() && gender != null && drinkingGoals > 0

    val isRecordPageRecordEnabled:Boolean
        get() = drinkingRecords > 0 && waterType.isNotBlank()
}



class WaterViewModel(app: Application) : AndroidViewModel(app) {

    private val localUserDao = AppDatabase.getInstance(app).localUserDao()
    private val localRecordDao = AppDatabase.getInstance(app).localRecordDao()
    private val db = FirebaseFirestore.getInstance()

    private fun nameKeyOf(name: String) = name.trim().lowercase()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /**
     * Project the local mirror (Room) into UiState.
     * recordList / recordIds come from the local `local_record` table, and
     * drinkingCount is DERIVED via SUM(amount) — never stored, so it always
     * matches the records (fixes cross-device / cross-day inconsistencies).
     */
    private suspend fun refreshFromLocal(key: String) {
        val records = localRecordDao.getRecords(key)
        // drinkingCount = sum of TODAY's records only -> auto-resets each day
        val total = localRecordDao.getTotalAmount(key, startOfTodayMillis())
        val uiList = records.map { UiRecord(it.amount, it.waterType, it.timestamp) }.toMutableList()
        val ids = records.map { it.recordId }.toMutableList()
        _uiState.update {
            it.copy(recordList = uiList, recordIds = ids, drinkingCount = total)
        }
    }

    /** Local midnight (00:00) of the current day, in epoch millis. */
    private fun startOfTodayMillis(): Long {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    /**
     * Login by name only.
     * - If user exists in Cloud (users/{nameKey}), load profile + records.
     * - If not exists, caller should navigate to Welcome page.
     */
    fun loginByName(
        nameInput: String,
        onExistingUser: () -> Unit,
        onNewUser: () -> Unit,
        onError: (Throwable) -> Unit = {}
    ) {
        val name = nameInput.trim()
        if (name.isBlank()) {
            onNewUser()
            return
        }
        val key = nameKeyOf(name)

        viewModelScope.launch {
            try {
                val userDoc = db.collection("users").document(key).get().await()
                if (!userDoc.exists()) {
                    // New user -> Welcome
                    _uiState.update { it.copy(name = name, currentUserKey = key) }
                    onNewUser()
                    return@launch
                }

                val cloudName = userDoc.getString("name") ?: name
                val cloudGender = userDoc.getString("gender")
                val cloudGoals = (userDoc.getLong("drinkingGoals") ?: 0L).toInt()

                val genderEnum = cloudGender?.let { runCatching { Gender.valueOf(it) }.getOrNull() }

                // Ensure a local profile row exists (mirror of cloud users/{key})
                val local = localUserDao.getUser(key)
                if (local == null) {
                    localUserDao.upsertUser(
                        LocalUserEntity(
                            nameKey = key,
                            name = cloudName,
                            gender = (genderEnum ?: Gender.Male).name
                        )
                    )
                }

                _uiState.update {
                    it.copy(
                        name = cloudName,
                        gender = genderEnum,
                        drinkingGoals = cloudGoals,
                        drinkingCount = 0, // recomputed from records below
                        currentUserKey = key
                    )
                }

                // Mirror records from cloud into Room, then derive count from them
                loadRecordsFromCloud()

                onExistingUser()
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }

    /** Create user after Welcome page (writes Cloud profile + creates local row). */
    fun createUserAfterWelcome(
        name: String,
        gender: Gender,
        drinkingGoals: Int,
        onDone: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        val cleanName = name.trim()
        val key = nameKeyOf(cleanName)

        viewModelScope.launch {
            try {
                // Cloud: users/{nameKey}
                val data = hashMapOf(
                    "name" to cleanName,
                    "gender" to gender.name,
                    "drinkingGoals" to drinkingGoals
                )
                db.collection("users").document(key).set(data).await()

                // Local: local_user profile mirror (no records yet -> count is 0)
                localUserDao.upsertUser(
                    LocalUserEntity(
                        nameKey = key,
                        name = cleanName,
                        gender = gender.name
                    )
                )

                _uiState.update {
                    it.copy(
                        name = cleanName,
                        gender = gender,
                        drinkingGoals = drinkingGoals,
                        drinkingCount = 0,
                        currentUserKey = key
                    )
                }

                onDone()
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }

    /**
     * Update current user's profile fields in Cloud: gender + drinkingGoals (name read-only).
     * - Writes to users/{currentUserKey}
     * - Updates uiState so Dashboard refreshes immediately
     */
    fun updateProfileGenderGoals(
        gender: Gender,
        goals: Int,
        onDone: () -> Unit,
        onError: (Throwable) -> Unit = {}
    ) {
        val key = _uiState.value.currentUserKey
        if (key.isBlank()) {
            onError(IllegalStateException("No current user"))
            return
        }

        viewModelScope.launch {
            try {
                val name = _uiState.value.name // keep name unchanged

                val data = hashMapOf(
                    "name" to name,
                    "gender" to gender.name,
                    "drinkingGoals" to goals
                )

                db.collection("users").document(key).set(data).await()

                // Local (Room): update the profile mirror's gender
                val local = localUserDao.getUser(key)
                if (local != null) {
                    localUserDao.upsertUser(
                        local.copy(gender = gender.name)
                    )
                } else {
                    // If local row missing for some reason, create it
                    localUserDao.upsertUser(
                        LocalUserEntity(
                            nameKey = key,
                            name = name,
                            gender = gender.name
                        )
                    )
                }

                _uiState.update {
                    it.copy(
                        gender = gender,
                        drinkingGoals = goals
                    )
                }

                onDone()
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }

    /** Load this user's records from Cloud (records where nameKey == currentUserKey). */
    fun loadRecordsFromCloud(onError: (Throwable) -> Unit = {}) {
        val key = _uiState.value.currentUserKey
        if (key.isBlank()) return

        viewModelScope.launch {
            try {
                val qs = db.collection("records")
                    .whereEqualTo("nameKey", key)
                    .get()
                    .await()

                // Map cloud docs -> local mirror entities
                val entities = qs.documents.map { d ->
                    LocalRecordEntity(
                        recordId = d.id,
                        nameKey = key,
                        amount = (d.getLong("drinkingRecords") ?: 0L).toInt(),
                        waterType = d.getString("waterType") ?: "",
                        timestamp = d.getLong("timestamp") ?: 0L
                    )
                }

                // Cloud is the source of truth: replace this user's local mirror
                localRecordDao.clearRecordsFor(key)
                localRecordDao.upsertRecords(entities)

                // Project mirror -> UI (drinkingCount derived via SUM)
                refreshFromLocal(key)
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }

    fun onNameChange(newName: String) {
        _uiState.value = _uiState.value.copy(name = newName)
    }

    fun onGenderSelected(g: Gender) {
        val current = _uiState.value
        val defaultGoal = updateDefaultDrinkingGoal(
            name = current.name,
            gender = g
        )
        _uiState.value = current.copy(
            gender = g,
            drinkingGoals = defaultGoal
        )
    }

    fun onDrinkingGoalsChange(goals: Int) {
        _uiState.value = _uiState.value.copy(drinkingGoals = goals)
    }

    private fun updateDefaultDrinkingGoal(
        name: String,
        gender: Gender?
    ): Int {
        return when {
            gender == Gender.Male && name.isNotBlank() -> 3700
            gender == Gender.Female && name.isNotBlank() -> 2700
            else -> 0
        }
    }

    fun checkDrinkingStatus(): String {
        val state = _uiState.value
        val gender = state.gender
        val goals = state.drinkingGoals

        return when {
            gender == Gender.Male && goals < 3700 -> "\uD83E\uDD64 You need to drink more water!"
            gender == Gender.Female && goals < 2700 -> "\uD83E\uDD64 You need to drink more water!"
            gender != null && goals > 5000 -> "\uD83D\uDE35\u200D\uD83D\uDCAB You are over the limit!"
            else -> "\uD83D\uDCAA You are doing great!"
        }
    }

    fun getPercentage(): Float {
        val state = _uiState.value
        if (state.drinkingGoals <= 0) {
            return 0f
        }
        return (state.drinkingCount.toFloat() / state.drinkingGoals.toFloat()).coerceIn(0f, 5f)
    }

    fun displayDrinkingAdvise(): String {
        return when {
            getPercentage() < 1f -> "You haven't drunk enough water today！"
            else -> "You are doing great!"
        }
    }

    fun drinkingRecordChanged(record: Int) {
        _uiState.value = _uiState.value.copy(drinkingRecords = record)
    }

    fun waterTypeChanged(type: String) {
        val normalized = type.trim()
        _uiState.update { it.copy(waterType = normalized) }
    }

    fun addRecord(onError: (Throwable) -> Unit = {}) {
        val currentState = _uiState.value
        if (!currentState.isRecordPageRecordEnabled) return

        val key = currentState.currentUserKey.ifBlank { nameKeyOf(currentState.name) }
        if (key.isBlank()) return

        val amount = currentState.drinkingRecords
        val wt = currentState.waterType
        val ts = System.currentTimeMillis()
        val recordId = UUID.randomUUID().toString()  //

        viewModelScope.launch {
            try {
                // 1) Cloud (source of truth): insert record
                val data = hashMapOf(
                    "nameKey" to key,
                    "drinkingRecords" to amount,
                    "waterType" to wt,
                    "timestamp" to ts
                )
                db.collection("records").document(recordId).set(data).await()

                // 2) Local mirror: insert the same record
                localRecordDao.upsertRecord(
                    LocalRecordEntity(
                        recordId = recordId,
                        nameKey = key,
                        amount = amount,
                        waterType = wt,
                        timestamp = ts
                    )
                )

                // 3) Reset inputs, then project mirror -> UI (count derived via SUM)
                _uiState.update {
                    it.copy(drinkingRecords = 0, waterType = "", currentUserKey = key)
                }
                refreshFromLocal(key)
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }

    fun deleteRecordAt(index: Int, onError: (Throwable) -> Unit = {}) {
        val currentState = _uiState.value
        val key = currentState.currentUserKey
        if (key.isBlank()) return
        if (index !in currentState.recordList.indices) return
        if (index !in currentState.recordIds.indices) return

        val recordId = currentState.recordIds[index]

        viewModelScope.launch {
            try {
                // 1) Cloud (source of truth): delete
                db.collection("records").document(recordId).delete().await()

                // 2) Local mirror: delete the same record
                localRecordDao.deleteRecord(recordId)

                // 3) Project mirror -> UI (count re-derived via SUM, can't go negative)
                refreshFromLocal(key)
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }


    fun returnLatestRecord(): Pair<Int, String> {
        val currentState = _uiState.value
        val recordList = currentState.recordList
        if (recordList.isNotEmpty()) {
            val last = recordList.last()
            return Pair(last.amount, last.waterType)
        }
        return Pair(0, "Error")
    }

    fun returnRecordMessage(): String {
        val currentState = _uiState.value
        if (currentState.drinkingCount < currentState.drinkingGoals / 2) return "You need to drink more water!"
        if (currentState.drinkingCount > currentState.drinkingGoals / 2 && currentState.drinkingCount < currentState.drinkingGoals) return "Come on! You only left ${currentState.drinkingGoals - currentState.drinkingCount}ml to go!"
        if (currentState.drinkingCount >= currentState.drinkingGoals) return "Congratulations! You've reached your goal!"
        else return "Error"
    }

    fun saveProfile(
        gender: Gender?,
        goalsText: String,
        onDone: () -> Unit,
        onErrorMessage: (String) -> Unit
    ) {
        val g = gender ?: run {
            onErrorMessage("Please select a gender")
            return
        }

        val goals = goalsText.trim().toIntOrNull() ?: 0
        if (goals <= 0) {
            onErrorMessage("Please enter a valid goal")
            return
        }

        updateProfileGenderGoals(
            gender = g,
            goals = goals,
            onDone = onDone,
            onError = { t ->
                onErrorMessage(t.message ?: "Update failed")
            }
        )
    }

    fun logout() {
        _uiState.update {
            it.copy(
                name = "",
                gender = null,
                drinkingGoals = 0,
                drinkingCount = 0,
                drinkingRecords = 0,
                waterType = "",
                recordList = mutableListOf(),
                recordIds = mutableListOf(),
                currentUserKey = ""
            )
        }
    }
}


class WaterViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WaterViewModel::class.java)) {
            return WaterViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
