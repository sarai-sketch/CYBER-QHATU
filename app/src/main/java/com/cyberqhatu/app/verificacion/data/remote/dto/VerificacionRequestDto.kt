package com.cyberqhatu.app.verificacion.data.remote.dto

data class VerificacionRequestDto(
    val idUsuario: String,
    val numeroCi: String,
    val similitudCalculada: Float
)
