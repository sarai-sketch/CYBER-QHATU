package com.cyberqhatu.app.core.network

/**
 * Ejecuta SQL directo contra SQLite Cloud por el endpoint /v2/sql
 * (el mismo camino que ya funciona para tiendas y usuarios).
 * Lanza ApiException si falla; ya no se esconden los errores.
 */
object SqliteCloudSql {

    /** Escapa comillas simples para usar un texto dentro de '...' en SQL. */
    fun esc(valor: String?): String = (valor ?: "").replace("'", "''")

    suspend fun ejecutar(sql: String): List<Map<String, Any?>> {
        val response = SqliteCloudClient.apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))
        if (!response.isSuccessful) {
            val detalle = response.errorBody()?.string()?.take(200).orEmpty()
            throw ApiException(response.code(), "SQL fallo (HTTP ${response.code()}) $detalle".trim())
        }
        val cuerpo = response.body()
            ?: throw ApiException(response.code(), "SQLite Cloud devolvio una respuesta vacia")
        val error = cuerpo.error
        if (error != null) throw ApiException(response.code(), error)
        return cuerpo.data.parseAsListOfMaps()
    }
}
