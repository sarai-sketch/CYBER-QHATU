package com.cyberqhatu.app.data.repository

import com.cyberqhatu.app.data.api.AuthApiService
import com.cyberqhatu.app.data.local.dao.AuthCacheDao
import com.cyberqhatu.app.data.local.entity.AuthCacheEntity
import com.cyberqhatu.app.data.model.AuthResponse
import com.cyberqhatu.app.data.model.LoginRequest
import com.cyberqhatu.app.data.model.RegisterRequest
import com.cyberqhatu.app.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

class AuthRepository(
    private val apiService: AuthApiService,
    private val cacheDao: AuthCacheDao
) {
    // Cache valid for 1 hour
    private val CACHE_EXPIRATION_MS = TimeUnit.HOURS.toMillis(1)

    fun loginWithCache(request: LoginRequest): Flow<Result<AuthResponse>> = flow {
        // 1. Try Cache First
        val cached = cacheDao.getAuthCacheByEmail(request.email)
        val currentTime = System.currentTimeMillis()
        
        if (cached != null && (currentTime - cached.timestamp) < CACHE_EXPIRATION_MS) {
            val user = User(id = cached.userId, email = cached.email, name = cached.userName)
            emit(Result.success(AuthResponse(cached.token, user)))
            return@flow
        }

        // 2. If no cache or expired, call API
        try {
            val response = apiService.login(request)
            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse != null) {
                    // 3. Save to Cache
                    val cacheEntity = AuthCacheEntity(
                        email = authResponse.user.email,
                        token = authResponse.token,
                        userId = authResponse.user.id,
                        userName = authResponse.user.name,
                        timestamp = currentTime
                    )
                    cacheDao.upsert(cacheEntity)
                    emit(Result.success(authResponse))
                } else {
                    emit(Result.failure(Exception("Respuesta vacía de la API")))
                }
            } else {
                emit(Result.failure(Exception("Error de API: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        return try {
            val response = apiService.register(request)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                val cacheEntity = AuthCacheEntity(
                    email = authResponse.user.email,
                    token = authResponse.token,
                    userId = authResponse.user.id,
                    userName = authResponse.user.name,
                    timestamp = System.currentTimeMillis()
                )
                cacheDao.upsert(cacheEntity)
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Registration failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearCache() {
        cacheDao.clearAll()
    }
}

