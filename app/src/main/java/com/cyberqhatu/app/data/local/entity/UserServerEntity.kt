package com.cyberqhatu.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "server_users")
data class UserServerEntity(
    @PrimaryKey
    val email: String,
    val passwordHash: String,
    val name: String?,
    val isEmailVerified: Boolean,
    val otpCode: String?,
    val otpExpiration: Long?
)
