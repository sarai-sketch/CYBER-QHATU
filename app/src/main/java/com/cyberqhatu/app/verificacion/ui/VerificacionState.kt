package com.cyberqhatu.app.verificacion.ui

import android.graphics.Bitmap
import com.cyberqhatu.app.verificacion.domain.ResultadoVerificacion

/**
 * Estados del flujo del proceso de verificación (Anverso C.I., Reverso C.I., Selfie).
 */
enum class PasoVerificacion {
    INSTRUCCIONES,
    CAPTURAR_CARNET_ANVERSO,
    CAPTURAR_CARNET_REVERSO,
    CAPTURAR_SELFIE,
    PROCESANDO,
    RESULTADO
}

/**
 * Estado UI para la pantalla de verificación.
 */
data class VerificacionUiState(
    val pasoActual: PasoVerificacion = PasoVerificacion.INSTRUCCIONES,
    val fotoCarnetAnverso: Bitmap? = null,
    val fotoCarnetReverso: Bitmap? = null,
    val fotoSelfie: Bitmap? = null,
    val estaCargando: Boolean = false,
    val resultado: ResultadoVerificacion? = null,
)
