package com.cyberqhatu.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberqhatu.app.data.local.entity.AuthCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(authCacheEntity: AuthCacheEntity)

    @Query("SELECT * FROM auth_cache WHERE email = :email LIMIT 1")
    suspend fun getAuthCacheByEmail(email: String): AuthCacheEntity?

    @Query("SELECT * FROM auth_cache WHERE email = :email LIMIT 1")
    fun observeAuthCacheByEmail(email: String): Flow<AuthCacheEntity?>

    @Query("DELETE FROM auth_cache WHERE email = :email")
    suspend fun deleteAuthCache(email: String)

    @Query("DELETE FROM auth_cache")
    suspend fun clearAll()
}
