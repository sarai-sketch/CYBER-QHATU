package com.cyberqhatu.app.verificacion.domain

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio para la verificación de identidad.
 * Abstrae el procesamiento biométrico, la persistencia en Room y la comunicación con la API remota.
 */
interface VerificacionRepository {
    suspend fun verificarIdentidad(
        fotoCarnet: Bitmap,
        fotoSelfie: Bitmap,
    ): ResultadoVerificacion

    fun obtenerEstadoVerificacionLocal(idUsuario: String = "USR-101"): Flow<ResultadoVerificacion?>
}
