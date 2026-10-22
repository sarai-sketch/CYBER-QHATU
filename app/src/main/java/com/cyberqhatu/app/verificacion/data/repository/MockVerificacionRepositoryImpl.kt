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
 * Implementación del repositorio mock para pruebas.
 */
class MockVerificacionRepositoryImpl(
    private val detectorRostro: DetectorRostroHelper = DetectorRostroHelper(),
    private val lectorTexto: LectorTextoCIHelper = LectorTextoCIHelper(),
    private val comparador: ComparadorBiometricoHelper = ComparadorBiometricoHelper(),
) : VerificacionRepository {

    override fun obtenerEstadoVerificacionLocal(idUsuario: String): Flow<ResultadoVerificacion?> {
        return flowOf(null)
    }

    override suspend fun verificarIdentidad(
        idUsuario: String,
        fotoCarnetAnverso: Bitmap,
        fotoCarnetReverso: Bitmap,
        fotoSelfie: Bitmap,
    ): ResultadoVerificacion {
        delay(1200)

        val ciAnverso = lectorTexto.extraerNumeroCI(fotoCarnetAnverso)
        val ciReverso = lectorTexto.extraerNumeroCI(fotoCarnetReverso)
        val numeroCi = ciAnverso ?: ciReverso ?: "7891234 LP"

        val rostroCarnet = detectorRostro.recortarRostro(fotoCarnetAnverso) ?: fotoCarnetAnverso
        val rostroSelfie = detectorRostro.recortarRostro(fotoSelfie) ?: fotoSelfie

        val similitud = comparador.compararRostros(rostroCarnet, rostroSelfie)
        val porcentajeFinal = if (similitud < 50f) 82.5f else similitud
        val esValido = porcentajeFinal >= 70.0f

        return ResultadoVerificacion(
            esExitoso = esValido,
            porcentajeSimilitud = porcentajeFinal,
            numeroCI = numeroCi,
            mensajeError = if (!esValido) "El rostro de la selfie no coincide con el carnet" else null,
        )
    }
}
