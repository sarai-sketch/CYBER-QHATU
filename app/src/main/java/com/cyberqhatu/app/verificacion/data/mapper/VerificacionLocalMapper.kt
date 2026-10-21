package com.cyberqhatu.app.verificacion.data.mapper

import com.cyberqhatu.app.data.local.entity.VerificacionEntity
import com.cyberqhatu.app.verificacion.domain.ResultadoVerificacion
import java.util.UUID

/**
 * Convierte entre entidades de Room (`VerificacionEntity`) y el modelo de dominio (`ResultadoVerificacion`).
 */
object VerificacionLocalMapper {

    fun toDomain(entity: VerificacionEntity?): ResultadoVerificacion? {
        if (entity == null) return null
        return ResultadoVerificacion(
            esExitoso = entity.esExitoso,
            porcentajeSimilitud = entity.porcentajeSimilitud,
            numeroCI = entity.numeroCI,
            mensajeError = if (!entity.esExitoso) "La verificación previa no fue aprobada" else null,
        )
    }

    fun toEntity(resultado: ResultadoVerificacion, idUsuario: String = "USR-101"): VerificacionEntity {
        return VerificacionEntity(
            idVerificacion = UUID.randomUUID().toString(),
            idUsuario = idUsuario,
            numeroCI = resultado.numeroCI,
            porcentajeSimilitud = resultado.porcentajeSimilitud,
            esExitoso = resultado.esExitoso,
            fechaVerificacion = System.currentTimeMillis(),
        )
    }
}
