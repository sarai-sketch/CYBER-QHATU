package com.cyberqhatu.app.verificacion.data.repository

import android.graphics.Bitmap
import android.util.Log
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Implementación de la arquitectura Offline-First (Fuente Única de Verdad).
 * Valida Anverso C.I., Reverso C.I. y Selfie en vivo.
 */
class VerificacionRepositoryImpl(
    private val verificacionCacheDao: VerificacionCacheDao,
    private val verificacionDao: VerificacionDao,
    private val apiService: VerificacionApiService,
    private val detectorRostro: DetectorRostroHelper = DetectorRostroHelper(),
    private val lectorTexto: LectorTextoCIHelper = LectorTextoCIHelper(),
    private val comparador: ComparadorBiometricoHelper = ComparadorBiometricoHelper(),
) : VerificacionRepository {

    private val CACHE_EXPIRATION_MS = TimeUnit.HOURS.toMillis(24) // Expiración de 24 horas

    override fun obtenerEstadoVerificacionLocal(idUsuario: String): Flow<ResultadoVerificacion?> {
        return verificacionCacheDao.obtenerCachePorUsuario(idUsuario).map { cacheEntity ->
            if (cacheEntity != null) {
                VerificacionCacheMapper.toDomain(cacheEntity)
            } else {
                null
            }
        }
    }

    override suspend fun verificarIdentidad(
        idUsuario: String,
        fotoCarnetAnverso: Bitmap,
        fotoCarnetReverso: Bitmap,
        fotoSelfie: Bitmap,
    ): ResultadoVerificacion {
        val cached = verificacionCacheDao.getCacheDirect(idUsuario)
        val currentTime = System.currentTimeMillis()

        // 1. Verificar si hay caché válida
        if (cached != null && (currentTime - cached.timestampCache) < CACHE_EXPIRATION_MS && cached.esExitoso) {
            val domain = VerificacionCacheMapper.toDomain(cached)
            if (domain != null) {
                Log.d("CACHE_TEST", "Desde CACHÉ")
                return domain
            }
        }

        // 2. Procesamiento local intensivo
        val resultadoLocal = withContext(Dispatchers.Default) {
            val ciAnverso = lectorTexto.extraerNumeroCI(fotoCarnetAnverso)
            val ciReverso = lectorTexto.extraerNumeroCI(fotoCarnetReverso)
            val numeroCi = ciAnverso ?: ciReverso

            if (numeroCi == null) {
                return@withContext ResultadoVerificacion(
                    esExitoso = false,
                    porcentajeSimilitud = 0.0f,
                    numeroCI = null,
                    mensajeError = "No se detectó un Carnet de Identidad de Bolivia ni en el anverso ni en el reverso. Toma fotos claras de tu C.I.",
                )
            }

            val rostroCarnet = detectorRostro.recortarRostro(fotoCarnetAnverso)
            if (rostroCarnet == null) {
                return@withContext ResultadoVerificacion(
                    esExitoso = false,
                    porcentajeSimilitud = 0.0f,
                    numeroCI = numeroCi,
                    mensajeError = "No se detectó la fotografía del rostro en el anverso del carnet. Asegúrate de encuadrar el frente de tu C.I.",
                )
            }

            val rostroSelfie = detectorRostro.recortarRostro(fotoSelfie)
            if (rostroSelfie == null) {
                return@withContext ResultadoVerificacion(
                    esExitoso = false,
                    porcentajeSimilitud = 0.0f,
                    numeroCI = numeroCi,
                    mensajeError = "No se detectó tu rostro en la selfie. Ubícate frente a la cámara con buena iluminación.",
                )
            }

            val similitudReal = comparador.compararRostros(rostroCarnet, rostroSelfie)
            val esValido = similitudReal >= 60.0f

            ResultadoVerificacion(
                esExitoso = esValido,
                porcentajeSimilitud = similitudReal,
                numeroCI = numeroCi,
                mensajeError = if (!esValido) "El rostro de la selfie no coincide con el anverso del carnet de identidad (Similitud: ${"%.1f".format(similitudReal)}%)." else null,
            )
        }

        if (!resultadoLocal.esExitoso) {
            return resultadoLocal
        }

        // 5. Llamada a la API y guardado en caché
        Log.d("CACHE_TEST", "Llamando a la API")
        try {
            val requestDto = VerificacionRequestDto(
                idUsuario = idUsuario,
                numeroCi = resultadoLocal.numeroCI,
                similitudCalculada = resultadoLocal.porcentajeSimilitud,
            )
            val response = apiService.verificarIdentidad(requestDto)
            if (response.isSuccessful && response.body() != null) {
                val resultadoRemoto = VerificacionMapper.toDomain(response.body()!!)
                val cacheEntity = VerificacionCacheMapper.toCacheEntity(resultadoRemoto, idUsuario, sincronizado = true).copy(timestampCache = System.currentTimeMillis())
                verificacionCacheDao.insertarCache(cacheEntity)
                verificacionDao.insertarVerificacion(
                    VerificacionLocalMapper.toEntity(resultadoRemoto, idUsuario),
                )
                Log.d("CACHE_TEST", "Guardado en caché")
                return resultadoRemoto
            } else {
                // isSuccessful == false -> caer a caché vieja si existe
                if (cached != null) {
                    val domain = VerificacionCacheMapper.toDomain(cached)
                    if (domain != null) return domain
                }
                return ResultadoVerificacion(false, 0f, null, "Error de validación del servidor: ${response.code()}")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // Falla de API, caer a caché vieja si existe
            if (cached != null) {
                val domain = VerificacionCacheMapper.toDomain(cached)
                if (domain != null) return domain
            }
            return ResultadoVerificacion(false, 0f, null, "Error de conexión al verificar identidad")
        }
    }
}
