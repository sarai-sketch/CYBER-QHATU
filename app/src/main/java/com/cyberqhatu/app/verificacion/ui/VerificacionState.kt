package com.cyberqhatu.app.verificacion.ui

import android.graphics.Bitmap
import com.cyberqhatu.app.verificacion.domain.ResultadoVerificacion

/**
 * Estados del flujo del proceso de verificación.
 */
enum class PasoVerificacion {
    INSTRUCCIONES,
    CAPTURAR_CARNET,
    CAPTURAR_SELFIE,
    PROCESANDO,
    RESULTADO
}

/**
 * Estado UI para la pantalla de verificación.
 */
data class VerificacionUiState(
    val pasoActual: PasoVerificacion = PasoVerificacion.INSTRUCCIONES,
    val fotoCarnet: Bitmap? = null,
    val fotoSelfie: Bitmap? = null,
    val estaCargando: Boolean = false,
    val resultado: ResultadoVerificacion? = null
)
