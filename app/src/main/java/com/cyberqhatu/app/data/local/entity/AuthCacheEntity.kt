package com.cyberqhatu.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "auth_cache")
data class AuthCacheEntity(
    @PrimaryKey
    val email: String,
    val userId: String,
    val userName: String?,
    val timestamp: Long
)
