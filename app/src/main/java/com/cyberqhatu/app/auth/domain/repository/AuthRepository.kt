package com.cyberqhatu.app.auth.domain.repository

interface AuthRepository {
    fun isUserLoggedInAndVerified(): Boolean
    fun getCurrentUserEmail(): String?
    fun getCurrentUserName(): String?
    fun logout()
    
    suspend fun login(email: String, contrasena: String): Result<Boolean>
    suspend fun register(nombre: String, email: String, contrasena: String): Result<Unit>
    suspend fun updateProfileName(newName: String): Result<Unit>
}
