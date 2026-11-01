package com.cyberqhatu.app.vendedor.data.remote.api

import com.cyberqhatu.app.vendedor.data.remote.dto.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Servicio Retrofit ApiService para conectar con las funciones API de SQLite Cloud.
 */
interface ProductoApiService {

    @POST("v2/functions/guardar_producto")
    suspend fun guardarProducto(
        @Body request: GuardarProductoRequestDto
    ): Response<GuardarProductoResponseDto>

    @POST("v2/functions/eliminar_producto")
    suspend fun eliminarProducto(
        @Body request: EliminarProductoRequestDto
    ): Response<ApiResponseDto>

    @POST("v2/functions/ocultar_producto")
    suspend fun ocultarProducto(
        @Body request: OcultarProductoRequestDto
    ): Response<ApiResponseDto>

    @POST("v2/functions/guardar_entrada_inventario")
    suspend fun guardarEntradaInventario(
        @Body request: EntradaInventarioRequestDto
    ): Response<ApiResponseDto>

    @POST("v2/functions/guardar_ajuste_inventario")
    suspend fun guardarAjusteInventario(
        @Body request: AjusteInventarioRequestDto
    ): Response<ApiResponseDto>

    @POST("v2/functions/guardar_venta")
    suspend fun guardarVenta(
        @Body request: GuardarVentaRequestDto
    ): Response<ApiResponseDto>

    @POST("v2/functions/eliminar_venta")
    suspend fun eliminarVenta(
        @Body request: EliminarVentaRequestDto
    ): Response<ApiResponseDto>
}

/**
 * Cliente singleton Retrofit configurado con la URL base de SQLite Cloud.
 */
object ProductoRetrofitClient {
    private const val BASE_URL = "https://clxgbyqcvz.g6.gateway.sqlite.cloud/"

    val instance: ProductoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductoApiService::class.java)
    }
}
