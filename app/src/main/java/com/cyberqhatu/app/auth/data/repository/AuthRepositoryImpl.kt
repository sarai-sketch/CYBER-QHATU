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
        return user != null && user.isEmailVerified
    }

    override fun getCurrentUserEmail(): String? = auth.currentUser?.email

    override fun getCurrentUserName(): String? = auth.currentUser?.displayName

    override fun logout() {
        auth.signOut()
    }

    override suspend fun login(email: String, contrasena: String): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        auth.signInWithEmailAndPassword(email, contrasena)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.reload()?.addOnCompleteListener { reloadTask ->
                        if (reloadTask.isSuccessful) {
                            if (user.isEmailVerified) {
                                continuation.resume(Result.success(true))
                            } else {
                                user.sendEmailVerification()
                                auth.signOut()
                                continuation.resume(Result.success(false))
                            }
                        } else {
                            auth.signOut()
                            continuation.resume(Result.failure(Exception("Error al verificar el estado de la cuenta.")))
                        }
                    }
                } else {
                    continuation.resume(Result.failure(task.exception ?: Exception("Error al iniciar sesión")))
                }
            }
    }

    override suspend fun register(nombre: String, email: String, contrasena: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        auth.createUserWithEmailAndPassword(email, contrasena)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(nombre)
                        .build()
                    
                    user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                        user.sendEmailVerification().addOnCompleteListener { emailTask ->
                            auth.signOut() // Cerrar sesión y esperar verificación
                            if (emailTask.isSuccessful) {
                                continuation.resume(Result.success(Unit))
                            } else {
                                continuation.resume(Result.failure(emailTask.exception ?: Exception("Error al enviar correo de verificación")))
                            }
                        }
                    }
                } else {
                    continuation.resume(Result.failure(task.exception ?: Exception("Error al registrar")))
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
                    continuation.resume(Result.failure(task.exception ?: Exception("Error al actualizar")))
                }
            }
        } else {
            continuation.resume(Result.failure(Exception("Usuario no válido o nombre vacío")))
        }
    }
}
