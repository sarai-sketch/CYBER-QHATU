package com.cyberqhatu.app.verificacion.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para enviar la solicitud de verificación al backend cuando esté disponible.
 */
data class VerificacionRequestDto(
    @SerializedName("id_usuario") val idUsuario: String,
    @SerializedName("numero_ci") val numeroCi: String?,
    @SerializedName("similitud_calculada") val similitudCalculada: Float,
)

/**
 * DTO de respuesta desde el servidor backend.
 */
data class VerificacionResponseDto(
    @SerializedName("status") val status: String,
    @SerializedName("verified") val verified: Boolean,
    @SerializedName("ci") val ci: String?,
    @SerializedName("similarity_score") val similarityScore: Float?,
    @SerializedName("message") val message: String?,
)
