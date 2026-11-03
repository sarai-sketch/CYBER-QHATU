package com.cyberqhatu.app.auth.domain.repository

interface AuthRepository {
    fun isUserLoggedInAndVerified(): Boolean
    fun getCurrentUserEmail(): String?
    fun getCurrentUserName(): String?
    fun logout()
    
    suspend fun login(email: String, contrasena: String): Result<Pair<Boolean, String>>
    suspend fun register(nombre: String, email: String, telefono: String, contrasena: String): Result<String>
    suspend fun updateProfileName(newName: String): Result<Unit>
}
