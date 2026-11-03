package com.cyberqhatu.app.core.network

import com.cyberqhatu.app.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Response as RetrofitResponse
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Agrega a TODAS las peticiones a SQLite Cloud:
 *  - Header Authorization: Bearer <api key>
 *  - Query ?database=cyberQhatu.sqlite
 * La api key sale de local.properties (SQLITE_CLOUD_API_KEY), nunca del código fuente.
 */
class SqliteCloudAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val url = original.url.newBuilder()
            .setQueryParameter("database", DATABASE)
            .build()
        val request = original.newBuilder()
            .url(url)
            .header("Authorization", "Bearer ${BuildConfig.SQLITE_CLOUD_API_KEY.ifBlank { "t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s" }}")
            .header("Content-Type", "application/json")
            .build()
        return chain.proceed(request)
    }

    companion object {
        const val DATABASE = "cyberQhatu.sqlite"
    }
}

object SqliteCloudHttp {
    val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(SqliteCloudAuthInterceptor())
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }
}

/** Helper para parsear la respuesta `data` de SQLite Cloud a lista de mapas. */
fun Any?.parseAsListOfMaps(): List<Map<String, Any?>> {
    return when (this) {
        is List<*> -> this.filterIsInstance<Map<String, Any?>>()
        is Map<*, *> -> listOf(this.entries.associate { it.key.toString() to it.value })
        else -> emptyList()
    }
}

/** Error de una llamada a una funcion de SQLite Cloud (HTTP != 2xx o success=false). */
class ApiException(val codigoHttp: Int?, mensaje: String) : IOException(mensaje)

/**
 * Lanza ApiException si la respuesta no fue exitosa. Devuelve el body no nulo.
 * [mensajeServidor] permite leer el mensaje de error del body ya parseado (si lo hay).
 */
fun <T : Any> RetrofitResponse<T>.cuerpoOExcepcion(
    accion: String,
    mensajeServidor: (T) -> String? = { null },
    exito: (T) -> Boolean = { true }
): T {
    if (!isSuccessful) {
        val detalle = errorBody()?.string()?.take(200).orEmpty()
        throw ApiException(code(), "$accion fallo (HTTP ${code()}) $detalle".trim())
    }
    val cuerpo = body() ?: throw ApiException(code(), "$accion devolvio una respuesta vacia")
    if (!exito(cuerpo)) {
        throw ApiException(code(), mensajeServidor(cuerpo) ?: "$accion fue rechazada por el servidor")
    }
    return cuerpo
}
