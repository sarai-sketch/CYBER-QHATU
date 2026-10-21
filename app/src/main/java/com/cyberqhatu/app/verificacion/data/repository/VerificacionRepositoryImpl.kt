package com.cyberqhatu.app.verificacion.data.repository

import android.graphics.Bitmap
import com.cyberqhatu.app.data.local.dao.VerificacionCacheDao
import com.cyberqhatu.app.data.local.dao.VerificacionDao
import com.cyberqhatu.app.verificacion.data.helper.ComparadorBiometricoHelper
import com.cyberqhatu.app.verificacion.data.helper.DetectorRostroHelper
import com.cyberqhatu.app.verificacion.data.helper.LectorTextoCIHelper
import com.cyberqhatu.app.verificacion.data.mapper.VerificacionCacheMapper
import com.cyberqhatu.app.verificacion.data.mapper.VerificacionLocalMapper
import com.cyberqhatu.app.verificacion.data.mapper.VerificacionMapper
import com.cyberqhatu.app.verificacion.data.remote.api.VerificacionApiService
import com.cyberqhatu.app.verificacion.data.remote.dto.VerificacionRequestDto
import com.cyberqhatu.app.verificacion.domain.ResultadoVerificacion
import com.cyberqhatu.app.verificacion.domain.VerificacionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Implementación de la arquitectura Offline-First (Fuente Única de Verdad).
 * Valida Anverso C.I., Reverso C.I. y Selfie en vivo.
 */
class VerificacionRepositoryImpl(
    private val verificacionCacheDao: VerificacionCacheDao? = null,
    private val verificacionDao: VerificacionDao? = null,
    private val apiService: VerificacionApiService? = null,
    private val detectorRostro: DetectorRostroHelper = DetectorRostroHelper(),
    private val lectorTexto: LectorTextoCIHelper = LectorTextoCIHelper(),
    private val comparador: ComparadorBiometricoHelper = ComparadorBiometricoHelper(),
) : VerificacionRepository {

    override fun obtenerEstadoVerificacionLocal(idUsuario: String): Flow<ResultadoVerificacion?> {
        return verificacionCacheDao?.obtenerCachePorUsuario(idUsuario)?.map { cacheEntity ->
            VerificacionCacheMapper.toDomain(cacheEntity)
        } ?: verificacionDao?.obtenerUltimaVerificacionPorUsuario(idUsuario)?.map { entity ->
            VerificacionLocalMapper.toDomain(entity)
        } ?: flowOf(null)
    }

    override suspend fun verificarIdentidad(
        fotoCarnetAnverso: Bitmap,
        fotoCarnetReverso: Bitmap,
        fotoSelfie: Bitmap,
    ): ResultadoVerificacion {
        delay(1200)

        // 1. Detección OCR del número de C.I. en Anverso o Reverso
        val ciAnverso = lectorTexto.extraerNumeroCI(fotoCarnetAnverso)
        val ciReverso = lectorTexto.extraerNumeroCI(fotoCarnetReverso)
        val numeroCi = ciAnverso ?: ciReverso

        if (numeroCi == null) {
            return ResultadoVerificacion(
                esExitoso = false,
                porcentajeSimilitud = 0.0f,
                numeroCI = null,
                mensajeError = "No se detectó un Carnet de Identidad de Bolivia ni en el anverso ni en el reverso. Toma fotos claras de tu C.I.",
            )
        }

        // 2. Detección del rostro en el anverso del carnet y en la selfie
        val rostroCarnet = detectorRostro.recortarRostro(fotoCarnetAnverso)
        if (rostroCarnet == null) {
            return ResultadoVerificacion(
                esExitoso = false,
                porcentajeSimilitud = 0.0f,
                numeroCI = numeroCi,
                mensajeError = "No se detectó la fotografía del rostro en el anverso del carnet. Asegúrate de encuadrar el frente de tu C.I.",
            )
        }

        val rostroSelfie = detectorRostro.recortarRostro(fotoSelfie)
        if (rostroSelfie == null) {
            return ResultadoVerificacion(
                esExitoso = false,
                porcentajeSimilitud = 0.0f,
                numeroCI = numeroCi,
                mensajeError = "No se detectó tu rostro en la selfie. Ubícate frente a la cámara con buena iluminación.",
            )
        }

        // 3. Similitud biométrica real entre la cara del carnet y la selfie
        val similitudReal = comparador.compararRostros(rostroCarnet, rostroSelfie)
        val esValido = similitudReal >= 60.0f

        val resultadoLocal = ResultadoVerificacion(
            esExitoso = esValido,
            porcentajeSimilitud = similitudReal,
            numeroCI = numeroCi,
            mensajeError = if (!esValido) "El rostro de la selfie no coincide con el anverso del carnet de identidad (Similitud: ${"%.1f".format(similitudReal)}%)." else null,
        )

        // 4. Si la verificación fue exitosa, escribir en la Caché Local de Room (Offline-First)
        if (esValido) {
            val cacheEntity = VerificacionCacheMapper.toCacheEntity(resultadoLocal, "USR-101", sincronizado = false)
            verificacionCacheDao?.insertarCache(cacheEntity)
            verificacionDao?.insertarVerificacion(
                VerificacionLocalMapper.toEntity(resultadoLocal, "USR-101"),
            )
        }

        // 5. Sincronización en segundo plano con la API remota (Retrofit)
        if (apiService != null && esValido) {
            try {
                val requestDto = VerificacionRequestDto(
                    idUsuario = "USR-101",
                    numeroCi = numeroCi,
                    similitudCalculada = similitudReal,
                )
                val response = apiService.verificarIdentidad(requestDto)
                if (response.isSuccessful && response.body() != null) {
                    val resultadoRemoto = VerificacionMapper.toDomain(response.body()!!)
                    return resultadoRemoto
                }
            } catch (_: Exception) {
                // Modo Offline
            }
        }

        return resultadoLocal
    }
}
