package com.cyberqhatu.app.verificacion.data.remote.api

import com.cyberqhatu.app.verificacion.data.remote.dto.VerificacionRequestDto
import com.cyberqhatu.app.verificacion.data.remote.dto.VerificacionResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interfaz Retrofit ApiService para el servicio de verificación de identidad.
 */
interface VerificacionApiService {

    @POST("api/v1/verificacion/procesar")
    suspend fun verificarIdentidad(
        @Body request: VerificacionRequestDto,
    ): Response<VerificacionResponseDto>
}
