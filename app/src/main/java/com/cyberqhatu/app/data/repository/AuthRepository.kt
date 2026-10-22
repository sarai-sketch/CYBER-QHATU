package com.cyberqhatu.app.data.repository

import com.cyberqhatu.app.data.api.AuthApiService
import com.cyberqhatu.app.data.local.TokenManager
import com.cyberqhatu.app.data.local.dao.AuthCacheDao
import com.cyberqhatu.app.data.local.entity.AuthCacheEntity
import com.cyberqhatu.app.data.model.AuthResponse
import com.cyberqhatu.app.data.model.LoginRequest
import com.cyberqhatu.app.data.model.RegisterRequest
import com.cyberqhatu.app.data.model.ResendCodeRequest
import com.cyberqhatu.app.data.model.User
import com.cyberqhatu.app.data.model.VerifyEmailRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

class AuthRepository(
    private val apiService: AuthApiService,
    private val cacheDao: AuthCacheDao,
    private val tokenManager: TokenManager
) {
    // Cache valid for 1 hour
    private val CACHE_EXPIRATION_MS = TimeUnit.HOURS.toMillis(1)

    // Solo se debe usar para restaurar sesión
    fun getCachedSession(email: String): Flow<Result<AuthResponse>> = flow {
        val cached = cacheDao.getAuthCacheByEmail(email)
        val token = tokenManager.getToken().firstOrNull()
        val currentTime = System.currentTimeMillis()
        
        if (cached != null && token != null && (currentTime - cached.timestamp) < CACHE_EXPIRATION_MS) {
            val user = User(id = cached.userId, email = cached.email, name = cached.userName)
            emit(Result.success(AuthResponse(token, user)))
        } else {
            emit(Result.failure(Exception("No valid cached session found")))
        }
    }

    fun login(request: LoginRequest): Flow<Result<AuthResponse>> = flow {
        // Siempre llama a la API
        try {
            val response = apiService.login(request)
            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse != null) {
                    val currentTime = System.currentTimeMillis()
                    // 3. Save to Cache
                    val cacheEntity = AuthCacheEntity(
                        email = authResponse.user.email,
                        userId = authResponse.user.id,
                        userName = authResponse.user.name,
                        timestamp = currentTime
                    )
                    cacheDao.upsert(cacheEntity)
                    tokenManager.saveToken(authResponse.token)
                    emit(Result.success(authResponse))
                } else {
                    emit(Result.failure(Exception("Respuesta vacía de la API")))
                }
            } else {
                emit(Result.failure(Exception("Error de API: ${response.code()}")))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun register(request: RegisterRequest): Result<String> {
        return try {
            val response = apiService.register(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.message)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Registration failed: ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyEmail(request: VerifyEmailRequest): Result<String> {
        return try {
            val response = apiService.verifyEmail(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.message)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Verification failed: ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resendCode(request: ResendCodeRequest): Result<String> {
        return try {
            val response = apiService.resendCode(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.message)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Resend code failed: ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearCache() {
        cacheDao.clearAll()
        tokenManager.clearToken()
    }
}


