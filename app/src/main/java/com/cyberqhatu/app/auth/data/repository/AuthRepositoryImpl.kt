package com.cyberqhatu.app.auth.data.repository

import com.cyberqhatu.app.auth.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AuthRepositoryImpl : AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    override fun isUserLoggedInAndVerified(): Boolean {
        val user = auth.currentUser
        return user != null
    }

    override fun getCurrentUserEmail(): String? = auth.currentUser?.email

    override fun getCurrentUserName(): String? = auth.currentUser?.displayName ?: auth.currentUser?.email?.substringBefore("@")

    override fun logout() {
        auth.signOut()
    }

    override suspend fun login(email: String, contrasena: String): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        auth.signInWithEmailAndPassword(email.trim(), contrasena)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(Result.success(true))
                } else {
                    continuation.resume(Result.failure(task.exception ?: Exception("Correo o contraseña incorrectos")))
                }
            }
    }

    override suspend fun register(nombre: String, email: String, contrasena: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        auth.createUserWithEmailAndPassword(email.trim(), contrasena)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(nombre.trim())
                        .build()

                    user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                        continuation.resume(Result.success(Unit))
                    } ?: continuation.resume(Result.success(Unit))
                } else {
                    continuation.resume(Result.failure(task.exception ?: Exception("Error al registrar cuenta")))
                }
            }
    }

    override suspend fun updateProfileName(newName: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val user = auth.currentUser
        if (user != null && newName.isNotBlank()) {
            val updates = UserProfileChangeRequest.Builder()
                .setDisplayName(newName.trim())
                .build()

            user.updateProfile(updates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(Result.success(Unit))
                } else {
                    continuation.resume(Result.failure(task.exception ?: Exception("Error al actualizar perfil")))
                }
            }
        } else {
            continuation.resume(Result.failure(Exception("Usuario no válido o nombre vacío")))
        }
    }
}
