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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

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

    private val CACHE_EXPIRATION_MS = TimeUnit.HOURS.toMillis(24) // Expiración de 24 horas

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
        val idUsuario = "USR-101"
        val cached = verificacionCacheDao?.getCacheDirect(idUsuario)
        val currentTime = System.currentTimeMillis()

        // 1. Verificar si hay caché válida
        if (cached != null && (currentTime - cached.timestampCache) < CACHE_EXPIRATION_MS && cached.esExitoso) {
            val domain = VerificacionCacheMapper.toDomain(cached)
            if (domain != null) return domain
        }

        delay(1200)

        // 2. Detección OCR del número de C.I. en Anverso o Reverso
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

        // 3. Detección del rostro en el anverso del carnet y en la selfie
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

        // 4. Similitud biométrica real entre la cara del carnet y la selfie
        val similitudReal = comparador.compararRostros(rostroCarnet, rostroSelfie)
        val esValido = similitudReal >= 60.0f

        val resultadoLocal = ResultadoVerificacion(
            esExitoso = esValido,
            porcentajeSimilitud = similitudReal,
            numeroCI = numeroCi,
            mensajeError = if (!esValido) "El rostro de la selfie no coincide con el anverso del carnet de identidad (Similitud: ${"%.1f".format(similitudReal)}%)." else null,
        )

        // 5. Llamada a la API y guardado en caché
        if (apiService != null && esValido) {
            try {
                val requestDto = VerificacionRequestDto(
                    idUsuario = idUsuario,
                    numeroCi = numeroCi,
                    similitudCalculada = similitudReal,
                )
                val response = apiService.verificarIdentidad(requestDto)
                if (response.isSuccessful && response.body() != null) {
                    val resultadoRemoto = VerificacionMapper.toDomain(response.body()!!)
                    val cacheEntity = VerificacionCacheMapper.toCacheEntity(resultadoRemoto, idUsuario, sincronizado = true).copy(timestampCache = System.currentTimeMillis())
                    verificacionCacheDao?.insertarCache(cacheEntity)
                    verificacionDao?.insertarVerificacion(
                        VerificacionLocalMapper.toEntity(resultadoRemoto, idUsuario),
                    )
                    return resultadoRemoto
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // Falla de API, caer a caché vieja si existe, si no, usar resultadoLocal
                if (cached != null) {
                    val domain = VerificacionCacheMapper.toDomain(cached)
                    if (domain != null) return domain
                }
            }
        }

        // 6. Si no hay API o falló sin caché previa, guardar resultadoLocal
        if (esValido) {
            val cacheEntity = VerificacionCacheMapper.toCacheEntity(resultadoLocal, idUsuario, sincronizado = false).copy(timestampCache = System.currentTimeMillis())
            verificacionCacheDao?.insertarCache(cacheEntity)
            verificacionDao?.insertarVerificacion(
                VerificacionLocalMapper.toEntity(resultadoLocal, idUsuario),
            )
        }

        return resultadoLocal
    }
}
