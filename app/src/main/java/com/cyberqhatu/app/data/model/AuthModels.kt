package com.cyberqhatu.app.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String? = null
)

data class User(
    val id: String,
    val email: String,
    val name: String? = null
)

data class VerifyEmailRequest(
    val email: String,
    val code: String
)

data class ResendCodeRequest(
    val email: String
)

data class BaseResponse(
    val message: String
)

data class AuthResponse(
    val token: String,
    val user: User
)
