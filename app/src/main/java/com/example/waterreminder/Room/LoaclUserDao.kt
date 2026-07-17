package com.example.waterreminder.Room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocalUserDao {

    @Query("SELECT * FROM local_user WHERE nameKey = :nameKey LIMIT 1")
    suspend fun getUser(nameKey: String): LocalUserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: LocalUserEntity)

    @Query("DELETE FROM local_user WHERE nameKey = :nameKey")
    suspend fun deleteUser(nameKey: String)

}