package com.cyberqhatu.app.core.network

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

// DTOs de Solicitud Genericos y Específicos
data class ConsultarFavoritosRequest(
    @SerializedName("id_key") val idKey: String = "fav_01"
)

data class ProductosFavoritosRequest(
    @SerializedName("id_key") val idKey: String = "fav_01",
    @SerializedName("original_id") val originalId: Int = 1,
    @SerializedName("tipo") val tipo: String = "PERSONAJE",
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("subtitulo") val subtitulo: String = "",
    @SerializedName("imagen_url") val imagenUrl: String = "",
    @SerializedName("detalle_info") val detalleInfo: String = ""
)

data class ResenasRequest(
    @SerializedName("filter") val filter: String = "a"
)

data class GuardarEliminarFavoritosRequest(
    @SerializedName("filter") val filter: String = "a"
)

data class ResenaCompradorRequest(
    @SerializedName("filter") val filter: String = "a"
)

data class GuardarUsuarioRequest(
    @SerializedName("id_usuario") val idUsuario: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("telefono") val telefono: String? = null
)

// Response DTO generico para Funciones Edge de SQLite Cloud
data class SqliteCloudFunctionResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("data") val data: Any? = null,
    @SerializedName("error") val error: String? = null,
    @SerializedName("codigo_invitacion") val codigoInvitacion: String? = null
)

/**
 * Servicio Retrofit unificado con cabecera Bearer y parametro database=cyberQhatu.sqlite
 * para las 28+ Edge Functions de SQLite Cloud. Se usa Map<String, String> para evitar comodines de tipo.
 */
interface SqliteCloudFunctionsApiService {

    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/consultar-favoritos?database=cyberQhatu.sqlite")
    suspend fun consultarFavoritos(
        @Body request: ConsultarFavoritosRequest
    ): Response<SqliteCloudFunctionResponse>

    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/guardar_eliminar-favoritos?database=cyberQhatu.sqlite")
    suspend fun guardarEliminarFavoritos(
        @Body request: GuardarEliminarFavoritosRequest
    ): Response<SqliteCloudFunctionResponse>

    // 1. Aceptar invitación
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/aceptar_invitacion?database=cyberQhatu.sqlite")
    suspend fun aceptarInvitacion(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 2. Cambiar rol colaborador
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/cambiar_rol_colaborador?database=cyberQhatu.sqlite")
    suspend fun cambiarRolColaborador(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 3. Eliminar colaborador
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/eliminar_colaborador?database=cyberQhatu.sqlite")
    suspend fun eliminarColaborador(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 4. Eliminar favorito producto
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/eliminar_favorito_producto?database=cyberQhatu.sqlite")
    suspend fun eliminarFavoritoProducto(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 5. Eliminar favorito tienda
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/eliminar_favorito_tienda?database=cyberQhatu.sqlite")
    suspend fun eliminarFavoritoTienda(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 6. Eliminar invitación
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/eliminar_invitacion?database=cyberQhatu.sqlite")
    suspend fun eliminarInvitacion(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 7. Eliminar producto
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/eliminar_producto?database=cyberQhatu.sqlite")
    suspend fun eliminarProducto(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 8. Eliminar venta
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/eliminar_venta?database=cyberQhatu.sqlite")
    suspend fun eliminarVenta(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 9. Estadísticas ventas
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/estadisticas_ventas?database=cyberQhatu.sqlite")
    suspend fun estadisticasVentas(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 10. Guardar ajuste inventario
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/guardar_ajuste_inventario?database=cyberQhatu.sqlite")
    suspend fun guardarAjusteInventario(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 11. Guardar colaborador
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/guardar_colaborador?database=cyberQhatu.sqlite")
    suspend fun guardarColaborador(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 12. Guardar entrada inventario
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/guardar_entrada_inventario?database=cyberQhatu.sqlite")
    suspend fun guardarEntradaInventario(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 13. Guardar favorito producto
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/guardar_favorito_producto?database=cyberQhatu.sqlite")
    suspend fun guardarFavoritoProducto(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 14. Guardar favorito tienda
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/guardar_favorito_tienda?database=cyberQhatu.sqlite")
    suspend fun guardarFavoritoTienda(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 15. Guardar producto
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/guardar_producto?database=cyberQhatu.sqlite")
    suspend fun guardarProducto(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 16. Guardar reporte evidencia
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/guardar_reporte_evidencia?database=cyberQhatu.sqlite")
    suspend fun guardarReporteEvidencia(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 17. Guardar reporte tienda
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/guardar_reporte_tienda?database=cyberQhatu.sqlite")
    suspend fun guardarReporteTienda(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 18. Guardar reseña
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/guardar_resena?database=cyberQhatu.sqlite")
    suspend fun guardarResena(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 19. Guardar usuario (sin contraseña)
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/guardar_usuario?database=cyberQhatu.sqlite")
    suspend fun guardarUsuario(@Body request: GuardarUsuarioRequest): Response<SqliteCloudFunctionResponse>

    // 20. Guardar venta
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/guardar_venta?database=cyberQhatu.sqlite")
    suspend fun guardarVenta(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 21. Listar colaboradores
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/listar_colaboradores?database=cyberQhatu.sqlite")
    suspend fun listarColaboradores(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 22. Listar historial de movimientos
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/listar_historial_movimientos?database=cyberQhatu.sqlite")
    suspend fun listarHistorialMovimientos(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 23. Listar productos
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/listar_productos?database=cyberQhatu.sqlite")
    suspend fun listarProductos(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 24. Obtener datos inicio
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/obtener_datos_inicio?database=cyberQhatu.sqlite")
    suspend fun obtenerDatosInicio(@Body params: Map<String, String> = emptyMap()): Response<SqliteCloudFunctionResponse>

    // 25. Obtener favoritos usuario
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/obtener_favoritos_usuario?database=cyberQhatu.sqlite")
    suspend fun obtenerFavoritosUsuario(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 26. Obtener reportes de moderación
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/obtener_reportes_moderacion?database=cyberQhatu.sqlite")
    suspend fun obtenerReportesModeracion(@Body params: Map<String, String> = emptyMap()): Response<SqliteCloudFunctionResponse>

    // 27. Obtener reseñas de la tienda
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/obtener_resenas_tienda?database=cyberQhatu.sqlite")
    suspend fun obtenerResenasTienda(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 28. Ocultar producto
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/ocultar_producto?database=cyberQhatu.sqlite")
    suspend fun ocultarProducto(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 29. Productos favoritos
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/productos-favoritos?database=cyberQhatu.sqlite")
    suspend fun productosFavoritos(@Body request: ProductosFavoritosRequest): Response<SqliteCloudFunctionResponse>

    // 30. Reseña comprador
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/resena-comprador?database=cyberQhatu.sqlite")
    suspend fun resenaComprador(@Body request: ResenaCompradorRequest): Response<SqliteCloudFunctionResponse>

    // 31. Reseñas
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/resenas?database=cyberQhatu.sqlite")
    suspend fun resenas(@Body request: ResenasRequest): Response<SqliteCloudFunctionResponse>

    // 32. Tiendas favoritas
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/tiendas-favoritos?database=cyberQhatu.sqlite")
    suspend fun tiendasFavoritos(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // 33. Guardar tienda
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/guardar_tienda?database=cyberQhatu.sqlite")
    suspend fun guardarTienda(@Body params: Map<String, String>): Response<SqliteCloudFunctionResponse>

    // Petición dinámica a cualquier Edge Function
    @Headers("Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s", "Content-Type: application/json")
    @POST("functions/{functionName}?database=cyberQhatu.sqlite")
    suspend fun ejecutarEdgeFunction(
        @Path("functionName") functionName: String,
        @Body params: Map<String, String> = mapOf("filter" to "a")
    ): Response<SqliteCloudFunctionResponse>
}

object SqliteCloudFunctionsClient {
    private const val BASE_URL = "https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/"

    val apiService: SqliteCloudFunctionsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SqliteCloudFunctionsApiService::class.java)
    }
}
