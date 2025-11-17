package com.example.waterreminder.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class Gender { Male, Female }
enum class WaterType {Warm, Ice, Hot}

data class UiState(
    val name: String = "",
    val gender: Gender? = null,
    val drinkingGoals: Int = 0,
    val drinkingCount: Int = 0,
    val drinkingRecords: Int = 0,
    val waterType: String = "",
    val recordList: MutableList<Pair<Int, String>> = mutableListOf<Pair<Int, String>>()
) {
    val isWelcomePageNextEnabled: Boolean
        get() = name.isNotBlank() && gender != null && drinkingGoals > 0

    val isRecordPageRecordEnabled:Boolean
        get() = drinkingRecords > 0 && waterType.isNotBlank()
}



class ViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

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

    fun getPercentage(): Float{
        val state = _uiState.value
        if (state.drinkingGoals <= 0) {
            return 0f
        }
        return (state.drinkingCount.toFloat() / state.drinkingGoals.toFloat()).coerceIn(0f, 5f)
    }

    fun displayDrinkingAdvise(): String{
        return when{
            getPercentage() < 1f -> "You haven't drunk enough water today！"
            else -> "You are doing great!"
        }
    }

    fun drinkingRecordChanged(record: Int){
        _uiState.value = _uiState.value.copy(drinkingRecords = record)
    }

    fun waterTypeChanged(type: String){
        when{
            type == "\uD83E\uDDCA Ice" -> _uiState.value = _uiState.value.copy(waterType = WaterType.Ice.toString())
            type == "☕ Warm" -> _uiState.value = _uiState.value.copy(waterType = WaterType.Warm.toString())
            type == "\uD83D\uDD25 Hot" -> _uiState.value = _uiState.value.copy(waterType = WaterType.Hot.toString())
            else -> _uiState.value = _uiState.value.copy(waterType = type)
        }
    }

    fun addRecord(){
        val currentState = _uiState.value
        if (currentState.isRecordPageRecordEnabled) {
            val newRecordList = currentState.recordList.toMutableList().apply {
                add(Pair(currentState.drinkingRecords, currentState.waterType))
            }
            _uiState.value = currentState.copy(
                drinkingCount = currentState.drinkingCount + currentState.drinkingRecords,
                recordList = newRecordList,

                drinkingRecords = 0,
                waterType = ""
            )
        }
    }
}


