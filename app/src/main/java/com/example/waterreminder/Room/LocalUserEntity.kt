package com.example.waterreminder.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_user")
data class LocalUserEntity(
    @PrimaryKey
    val nameKey: String,
    val name: String,
    val gender: String
)