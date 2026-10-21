package com.cyberqhatu.app.verificacion.domain

/**
 * Representa el resultado final del proceso de verificación de identidad con biometría.
 */
data class ResultadoVerificacion(
    val esExitoso: Boolean,
    val porcentajeSimilitud: Float,
    val numeroCI: String? = null,
    val mensajeError: String? = null,
)
