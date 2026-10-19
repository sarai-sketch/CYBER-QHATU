package com.cyberqhatu.app.verificacion.domain

import android.graphics.Bitmap

/**
 * Contrato del repositorio para la verificación de identidad.
 * Abstrae el procesamiento biométrico y la futura comunicación con la API remota.
 */
interface VerificacionRepository {
    suspend fun verificarIdentidad(
        fotoCarnet: Bitmap,
        fotoSelfie: Bitmap,
    ): ResultadoVerificacion
}
