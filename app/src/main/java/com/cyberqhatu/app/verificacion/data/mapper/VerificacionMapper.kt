package com.cyberqhatu.app.verificacion.data.mapper

import com.cyberqhatu.app.verificacion.data.remote.dto.VerificacionResponseDto
import com.cyberqhatu.app.verificacion.domain.ResultadoVerificacion

/**
 * Convierte DTOs de red a modelos de dominio de la aplicación.
 */
object VerificacionMapper {
    fun toDomain(dto: VerificacionResponseDto): ResultadoVerificacion {
        return ResultadoVerificacion(
            esExitoso = dto.verified,
            porcentajeSimilitud = dto.similarityScore ?: 0.0f,
            numeroCI = dto.ci,
            mensajeError = dto.message,
        )
    }
}
