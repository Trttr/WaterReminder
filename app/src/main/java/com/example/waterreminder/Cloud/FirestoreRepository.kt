package com.example.waterreminder.Cloud

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

data class CloudUser(
    val name: String = "",
    val gender: String = "",
    val drinkingGoals: Int = 0
)

data class CloudRecord(
    val recordId: String = "",
    val nameKey: String = "",
    val drinkingRecords: Int = 0,
    val waterType: String = "",
    val timestamp: Long = 0L
)

class FirestoreRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val usersCol = db.collection("users")
    private val recordsCol = db.collection("records")

    fun makeNameKey(name: String): String = name.trim().lowercase()

    // ---------- Users ----------

    suspend fun getUser(nameKey: String): CloudUser? {
        val doc = usersCol.document(nameKey).get().await()
        if (!doc.exists()) return null
        return CloudUser(
            name = doc.getString("name") ?: "",
            gender = doc.getString("gender") ?: "",
            drinkingGoals = (doc.getLong("drinkingGoals") ?: 0L).toInt()
        )
    }

    suspend fun upsertUser(nameKey: String, user: CloudUser) {
        val data = hashMapOf(
            "name" to user.name,
            "gender" to user.gender,
            "drinkingGoals" to user.drinkingGoals
        )
        usersCol.document(nameKey).set(data).await()
    }

    suspend fun deleteUser(nameKey: String) {
        // 先删所有 records（Firestore 没有自动级联）
        val qs = recordsCol.whereEqualTo("nameKey", nameKey).get().await()
        val batch = db.batch()
        qs.documents.forEach { batch.delete(it.reference) }
        batch.delete(usersCol.document(nameKey))
        batch.commit().await()
    }

    // ---------- Records ----------

    suspend fun addRecord(
        nameKey: String,
        amount: Int,
        waterType: String,
        timestamp: Long = System.currentTimeMillis(),
        recordId: String = UUID.randomUUID().toString()
    ): String {
        val data = hashMapOf(
            "nameKey" to nameKey,
            "drinkingRecords" to amount,
            "waterType" to waterType,
            "timestamp" to timestamp
        )
        recordsCol.document(recordId).set(data).await()
        return recordId
    }

    suspend fun deleteRecord(recordId: String) {
        recordsCol.document(recordId).delete().await()
    }

    suspend fun loadRecordsForUser(nameKey: String): List<CloudRecord> {
        val qs = recordsCol
            .whereEqualTo("nameKey", nameKey)
            .orderBy("timestamp")
            .get()
            .await()

        return qs.documents.map { d ->
            CloudRecord(
                recordId = d.id,
                nameKey = d.getString("nameKey") ?: "",
                drinkingRecords = (d.getLong("drinkingRecords") ?: 0L).toInt(),
                waterType = d.getString("waterType") ?: "",
                timestamp = d.getLong("timestamp") ?: 0L
            )
        }
    }
}