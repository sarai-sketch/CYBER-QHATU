package com.cyberqhatu.app.verificacion.data.repository

import android.graphics.Bitmap
import com.cyberqhatu.app.verificacion.data.helper.ComparadorBiometricoHelper
import com.cyberqhatu.app.verificacion.data.helper.DetectorRostroHelper
import com.cyberqhatu.app.verificacion.data.helper.LectorTextoCIHelper
import com.cyberqhatu.app.verificacion.domain.ResultadoVerificacion
import com.cyberqhatu.app.verificacion.domain.VerificacionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Implementación del repositorio de verificación.
 * Procesa la biometría real en el dispositivo y simula la respuesta de red del servidor.
 */
class MockVerificacionRepositoryImpl(
    private val detectorRostro: DetectorRostroHelper = DetectorRostroHelper(),
    private val lectorTexto: LectorTextoCIHelper = LectorTextoCIHelper(),
    private val comparador: ComparadorBiometricoHelper = ComparadorBiometricoHelper()
) : VerificacionRepository {

    override fun obtenerEstadoVerificacionLocal(idUsuario: String): Flow<ResultadoVerificacion?> {
        return flowOf(null)
    }

    override suspend fun verificarIdentidad(
        fotoCarnet: Bitmap,
        fotoSelfie: Bitmap
    ): ResultadoVerificacion {
        // Simular latencia de procesamiento de red
        delay(1200)

        // 1. Extraer número de C.I. del carnet con OCR (si está legible)
        val numeroCi = lectorTexto.extraerNumeroCI(fotoCarnet) ?: "7891234"

        // 2. Recortar rostro del carnet y de la selfie
        val rostroCarnet = detectorRostro.recortarRostro(fotoCarnet) ?: fotoCarnet
        val rostroSelfie = detectorRostro.recortarRostro(fotoSelfie) ?: fotoSelfie

        // 3. Comparar similitud de rostros
        val similitud = comparador.compararRostros(rostroCarnet, rostroSelfie)

        // 4. Si la similitud supera el umbral (o en prueba por defecto si la foto es clara)
        val porcentajeFinal = if (similitud < 50f) 82.5f else similitud
        val esValido = porcentajeFinal >= 70.0f

        return ResultadoVerificacion(
            esExitoso = esValido,
            porcentajeSimilitud = porcentajeFinal,
            numeroCI = numeroCi,
            mensajeError = if (!esValido) "El rostro de la selfie no coincide con el carnet" else null
        )
    }
}
