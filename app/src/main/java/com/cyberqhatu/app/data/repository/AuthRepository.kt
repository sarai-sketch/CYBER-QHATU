package com.cyberqhatu.app.data.repository

import com.cyberqhatu.app.data.model.AuthResponse
import com.cyberqhatu.app.data.model.LoginRequest
import com.cyberqhatu.app.data.model.RegisterRequest
import com.cyberqhatu.app.data.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {
    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val result = auth.signInWithEmailAndPassword(request.email, request.password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                val token = firebaseUser.uid 
                val user = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    name = firebaseUser.displayName
                )
                Result.success(AuthResponse(token, user))
            } else {
                Result.failure(Exception("Login failed: User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        return try {
            val result = auth.createUserWithEmailAndPassword(request.email, request.password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                val token = firebaseUser.uid
                val user = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    name = request.name ?: firebaseUser.displayName
                )
                Result.success(AuthResponse(token, user))
            } else {
                Result.failure(Exception("Registration failed: User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
