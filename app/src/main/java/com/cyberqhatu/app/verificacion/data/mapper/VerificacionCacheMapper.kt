package com.cyberqhatu.app.verificacion.data.mapper

import com.cyberqhatu.app.data.local.entity.VerificacionCacheEntity
import com.cyberqhatu.app.verificacion.domain.ResultadoVerificacion
import java.util.UUID

/**
 * Mapeador de Caché para convertir entre la entidad de caché de Room y los modelos de dominio.
 */
object VerificacionCacheMapper {

    fun toDomain(cacheEntity: VerificacionCacheEntity?): ResultadoVerificacion? {
        if (cacheEntity == null) return null
        return ResultadoVerificacion(
            esExitoso = cacheEntity.esExitoso,
            porcentajeSimilitud = cacheEntity.porcentajeSimilitud,
            numeroCI = cacheEntity.numeroCI,
            mensajeError = if (!cacheEntity.esExitoso) "Caché: Verificación no aprobada" else null,
        )
    }

    fun toCacheEntity(
        resultado: ResultadoVerificacion,
        idUsuario: String = "USR-101",
        sincronizado: Boolean = false,
    ): VerificacionCacheEntity {
        return VerificacionCacheEntity(
            idVerificacion = UUID.randomUUID().toString(),
            idUsuario = idUsuario,
            numeroCI = resultado.numeroCI,
            porcentajeSimilitud = resultado.porcentajeSimilitud,
            esExitoso = resultado.esExitoso,
            timestampCache = System.currentTimeMillis(),
            sincronizadoServidor = sincronizado,
        )
    }
}
