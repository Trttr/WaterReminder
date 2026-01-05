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

    @Query("UPDATE local_user SET drinkingCount = drinkingCount + :amount WHERE nameKey = :nameKey")
    suspend fun increaseCount(nameKey: String, amount: Int)

    @Query("UPDATE local_user SET drinkingCount = drinkingCount - :amount WHERE nameKey = :nameKey")
    suspend fun decreaseCount(nameKey: String, amount: Int)

    @Query("DELETE FROM local_user WHERE nameKey = :nameKey")
    suspend fun deleteUser(nameKey: String)

    @Query("UPDATE local_user SET drinkingCount = :count WHERE nameKey = :key")
    suspend fun setCount(key: String, count: Int)

}