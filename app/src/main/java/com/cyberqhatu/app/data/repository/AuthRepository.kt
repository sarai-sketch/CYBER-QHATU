package com.cyberqhatu.app.data.repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun register(name: String, email: String, password: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Error al crear el usuario")

            val profileUpdates = userProfileChangeRequest {
                displayName = name
            }
            user.updateProfile(profileUpdates).await()
            user.sendEmailVerification().await()

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(mapFirebaseException(e))
        }
    }

    suspend fun login(email: String, password: String): Result<Boolean> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Error al iniciar sesión")
            user.reload().await()
            Result.success(user.isEmailVerified)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(mapFirebaseException(e))
        }
    }

    suspend fun sendVerificationEmail(): Result<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("No hay un usuario activo")
            user.sendEmailVerification().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(mapFirebaseException(e))
        }
    }

    suspend fun reloadAndCheckVerified(): Result<Boolean> {
        return try {
            val user = auth.currentUser ?: return Result.success(false)
            user.reload().await()
            Result.success(user.isEmailVerified)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(mapFirebaseException(e))
        }
    }

    fun logout() {
        auth.signOut()
    }

    private fun mapFirebaseException(e: Exception): Exception {
        val message = when (e) {
            is FirebaseAuthUserCollisionException -> "Este correo ya está registrado"
            is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil (mínimo 6 caracteres; recomendamos 8 o más)"
            is FirebaseAuthInvalidCredentialsException, is FirebaseAuthInvalidUserException -> "Correo o contraseña incorrectos"
            is FirebaseTooManyRequestsException -> "Demasiados intentos, espera unos minutos"
            is FirebaseNetworkException -> "Sin conexión a internet"
            else -> e.message ?: "Ocurrió un error inesperado"
        }
        return Exception(message)
    }
}
