package com.cyberqhatu.app.core.network

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

// Request DTOs
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

data class SqliteCloudFunctionRequest(
    @SerializedName("filter") val filter: String = "a",
    @SerializedName("id_producto") val idProducto: String? = null,
    @SerializedName("id_tienda") val idTienda: String? = null,
    @SerializedName("id_usuario") val idUsuario: String? = null,
    @SerializedName("nombre") val nombre: String? = null,
    @SerializedName("descripcion") val descripcion: String? = null,
    @SerializedName("precio_bs") val precioBs: Double? = null,
    @SerializedName("stock") val stock: Int? = null
)

// Response DTO generico para Funciones Edge
data class SqliteCloudFunctionResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("data") val data: List<Map<String, Any?>>? = null,
    @SerializedName("error") val error: String? = null
)

interface SqliteCloudFunctionsApiService {

    @Headers(
        "Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s",
        "Content-Type: application/json"
    )
    @POST("functions/consultar-favoritos?database=cyberQhatu.sqlite")
    suspend fun consultarFavoritos(
        @Body request: ConsultarFavoritosRequest
    ): Response<SqliteCloudFunctionResponse>

    @Headers(
        "Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s",
        "Content-Type: application/json"
    )
    @POST("functions/productos-favoritos?database=cyberQhatu.sqlite")
    suspend fun productosFavoritos(
        @Body request: ProductosFavoritosRequest
    ): Response<SqliteCloudFunctionResponse>

    @Headers(
        "Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s",
        "Content-Type: application/json"
    )
    @POST("functions/resenas?database=cyberQhatu.sqlite")
    suspend fun resenas(
        @Body request: ResenasRequest
    ): Response<SqliteCloudFunctionResponse>

    @Headers(
        "Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s",
        "Content-Type: application/json"
    )
    @POST("functions/guardar_eliminar-favoritos?database=cyberQhatu.sqlite")
    suspend fun guardarEliminarFavoritos(
        @Body request: GuardarEliminarFavoritosRequest
    ): Response<SqliteCloudFunctionResponse>

    @Headers(
        "Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s",
        "Content-Type: application/json"
    )
    @POST("functions/resena-comprador?database=cyberQhatu.sqlite")
    suspend fun resenaComprador(
        @Body request: ResenaCompradorRequest
    ): Response<SqliteCloudFunctionResponse>

    // Peticiones dinamicas a Edge Functions especificando la base de datos cyberQhatu.sqlite
    @Headers(
        "Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s",
        "Content-Type: application/json"
    )
    @POST("functions/{functionName}?database=cyberQhatu.sqlite")
    suspend fun ejecutarEdgeFunction(
        @Path("functionName") functionName: String,
        @Body params: Map<String, Any?> = mapOf("filter" to "a")
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
