package com.cyberqhatu.app.verificacion.data.remote.dto

data class VerificacionResponseDto(
    val verified: Boolean = false,
    val similarityScore: Float? = null,
    val ci: String? = null,
    val message: String? = null
)
