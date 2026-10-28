package com.cyberqhatu.app.feature.tienda.data.repository

import android.util.Log
import com.cyberqhatu.app.core.network.SqliteCloudClient
import com.cyberqhatu.app.core.network.SqliteCloudQueryRequest
import com.cyberqhatu.app.tienda.Tienda

class SqliteCloudTiendaRepository {

    private val apiService = SqliteCloudClient.apiService

    suspend fun guardarTiendaRemota(tienda: Tienda): Boolean {
        return try {
            val sql = "INSERT OR REPLACE INTO tiendas (id_tienda, id_propietario, nombre, descripcion, tipo, direccion, link_whatsapp, estado) VALUES ('${tienda.idTienda}', '${tienda.idPropietario}', '${tienda.nombre.replace("'", "''")}', '${tienda.descripcion.replace("'", "''")}', '${tienda.tipo.name}', '${(tienda.direccionGoogleMaps ?: "").replace("'", "''")}', '${tienda.linkWhatsapp ?: ""}', 'ACTIVA');"
            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error guardando tienda remota: ${e.message}", e)
            false
        }
    }
}
