package com.cyberqhatu.app.vendedor.data.remote.api

import com.cyberqhatu.app.vendedor.data.remote.dto.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Servicio Retrofit ApiService para conectar con las funciones API de colaboradores e invitaciones en SQLite Cloud.
 */
interface ColaboradorApiService {

    @POST("v2/functions/guardar_colaborador")
    suspend fun guardarColaborador(
        @Body request: GuardarColaboradorRequestDto
    ): Response<GuardarColaboradorResponseDto>

    @POST("v2/functions/aceptar_invitacion")
    suspend fun aceptarInvitacion(
        @Body request: AceptarInvitacionRequestDto
    ): Response<ApiResponseDto>

    @POST("v2/functions/eliminar_colaborador")
    suspend fun eliminarColaborador(
        @Body request: EliminarColaboradorRequestDto
    ): Response<ApiResponseDto>

    @POST("v2/functions/eliminar_invitacion")
    suspend fun eliminarInvitacion(
        @Body request: EliminarInvitacionRequestDto
    ): Response<ApiResponseDto>

    @POST("v2/functions/cambiar_rol_colaborador")
    suspend fun cambiarRolColaborador(
        @Body request: CambiarRolColaboradorRequestDto
    ): Response<ApiResponseDto>
}

/**
 * Cliente singleton Retrofit configurado para la API de colaboradores en SQLite Cloud.
 */
object ColaboradorRetrofitClient {
    private const val BASE_URL = "https://clxgbyqcvz.g6.gateway.sqlite.cloud/"

    val instance: ColaboradorApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ColaboradorApiService::class.java)
    }
}
