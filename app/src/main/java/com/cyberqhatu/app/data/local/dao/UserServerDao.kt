package com.cyberqhatu.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cyberqhatu.app.data.local.entity.UserServerEntity

@Dao
interface UserServerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserServerEntity)

    @Update
    suspend fun updateUser(user: UserServerEntity)

    @Query("SELECT * FROM server_users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserServerEntity?
}
