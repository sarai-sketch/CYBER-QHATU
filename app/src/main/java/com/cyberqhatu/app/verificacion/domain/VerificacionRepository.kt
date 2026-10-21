package com.cyberqhatu.app.verificacion.domain

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio para la verificación de identidad (Anverso, Reverso y Selfie).
 */
interface VerificacionRepository {
    suspend fun verificarIdentidad(
        fotoCarnetAnverso: Bitmap,
        fotoCarnetReverso: Bitmap,
        fotoSelfie: Bitmap,
    ): ResultadoVerificacion

    fun obtenerEstadoVerificacionLocal(idUsuario: String = "USR-101"): Flow<ResultadoVerificacion?>
}
