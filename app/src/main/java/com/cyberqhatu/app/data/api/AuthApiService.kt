package com.cyberqhatu.app.data.api

import com.cyberqhatu.app.data.model.AuthResponse
import com.cyberqhatu.app.data.model.BaseResponse
import com.cyberqhatu.app.data.model.LoginRequest
import com.cyberqhatu.app.data.model.RegisterRequest
import com.cyberqhatu.app.data.model.ResendCodeRequest
import com.cyberqhatu.app.data.model.VerifyEmailRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<BaseResponse>

    @POST("api/auth/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequest): Response<BaseResponse>

    @POST("api/auth/resend-code")
    suspend fun resendCode(@Body request: ResendCodeRequest): Response<BaseResponse>
}
