package com.cyberqhatu.app.feature.tienda.data.repository

import android.util.Log
import com.cyberqhatu.app.core.network.SqliteCloudClient
import com.cyberqhatu.app.core.network.SqliteCloudQueryRequest
import com.cyberqhatu.app.tienda.Tienda
import com.cyberqhatu.app.tienda.TipoTienda
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SqliteCloudTiendaRepository {

    private val apiService = SqliteCloudClient.apiService

    fun obtenerTiendasRemotas(): Flow<List<Tienda>> = flow {
        try {
            val sql = "SELECT id_tienda, id_propietario, nombre, descripcion, tipo, direccion, link_whatsapp, estado FROM tiendas WHERE estado = 'ACTIVA';"
            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))

            if (response.isSuccessful && response.body()?.data != null) {
                val lista = response.body()!!.data!!.mapNotNull { row ->
                    val idTienda = row["id_tienda"]?.toString() ?: return@mapNotNull null
                    val idProp = row["id_propietario"]?.toString() ?: "u1"
                    val nombre = row["nombre"]?.toString() ?: "Tienda"
                    val desc = row["descripcion"]?.toString() ?: ""
                    val tipoStr = row["tipo"]?.toString() ?: "FISICA"
                    val dir = row["direccion"]?.toString()
                    val wa = row["link_whatsapp"]?.toString()

                    val tipoEnum = try {
                        TipoTienda.valueOf(tipoStr)
                    } catch (_: Exception) {
                        TipoTienda.FISICA
                    }

                    Tienda(
                        idTienda = idTienda,
                        idPropietario = idProp,
                        nombre = nombre,
                        descripcion = desc,
                        logo = null,
                        tipo = tipoEnum,
                        direccionGoogleMaps = dir,
                        linkWhatsapp = wa
                    )
                }
                emit(lista)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error consultando tiendas remotas: ${e.message}", e)
            emit(emptyList())
        }
    }

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
