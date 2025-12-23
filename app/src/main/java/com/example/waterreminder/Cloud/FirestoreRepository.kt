package com.example.waterreminder.Cloud


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
