package com.cyberqhatu.app.vendedor.data.remote.api

import com.cyberqhatu.app.vendedor.data.remote.dto.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Servicio Retrofit ApiService para consultar catálogo, historial, miembros y métricas en SQLite Cloud.
 */
interface ConsultaApiService {

    @POST("v2/functions/listar_productos")
    suspend fun listarProductos(
        @Body request: ListarProductosRequestDto
    ): Response<List<GuardarProductoRequestDto>>

    @POST("v2/functions/listar_historial_movimientos")
    suspend fun listarHistorialMovimientos(
        @Body request: ListarMovimientosRequestDto
    ): Response<List<Any>>

    @POST("v2/functions/listar_colaboradores")
    suspend fun listarColaboradores(
        @Body request: ListarColaboradoresRequestDto
    ): Response<List<Any>>

    @POST("v2/functions/estadisticas_ventas")
    suspend fun obtenerEstadisticasVentas(
        @Body request: EstadisticasVentasRequestDto
    ): Response<EstadisticasVentasResponseDto>
}

/**
 * Cliente singleton Retrofit configurado para consultas de rendimiento y catálogo en SQLite Cloud.
 */
object ConsultaRetrofitClient {
    private const val BASE_URL = "https://clxgbyqcvz.g6.gateway.sqlite.cloud/"

    val instance: ConsultaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ConsultaApiService::class.java)
    }
}
