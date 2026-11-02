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
            val sql = "SELECT id_tienda, id_propietario, nombre, descripcion, tipo, direccion_google_maps, horario_apertura, horario_cierre, link_whatsapp, estado, strikes FROM tiendas WHERE estado = 'ACTIVA';"
            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))

            if (response.isSuccessful && response.body()?.data != null) {
                val lista = response.body()!!.data!!.mapNotNull { row ->
                    val idTienda = row["id_tienda"]?.toString() ?: return@mapNotNull null
                    val idProp = row["id_propietario"]?.toString() ?: "u1"
                    val nombre = row["nombre"]?.toString() ?: "Tienda"
                    val desc = row["descripcion"]?.toString() ?: ""
                    val tipoStr = row["tipo"]?.toString() ?: "FISICA"
                    val dir = row["direccion_google_maps"]?.toString()
                    val apertura = row["horario_apertura"]?.toString()
                    val cierre = row["horario_cierre"]?.toString()
                    val wa = row["link_whatsapp"]?.toString()
                    val strikes = (row["strikes"] as? Double)?.toInt() ?: 0

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
                        horarioApertura = apertura,
                        horarioCierre = cierre,
                        linkWhatsapp = wa,
                        strikes = strikes
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
            val dirStr = if (tienda.direccionGoogleMaps != null) "'${tienda.direccionGoogleMaps!!.replace("'", "''")}'" else "NULL"
            val apertura = if (tienda.horarioApertura != null) "'${tienda.horarioApertura}'" else "NULL"
            val cierre = if (tienda.horarioCierre != null) "'${tienda.horarioCierre}'" else "NULL"

            val sql = """
                INSERT OR REPLACE INTO tiendas (
                    id_tienda, id_propietario, nombre, descripcion, tipo, 
                    direccion_google_maps, horario_apertura, horario_cierre, link_whatsapp, estado, strikes
                ) VALUES (
                    '${tienda.idTienda}', '${tienda.idPropietario}', '${tienda.nombre.replace("'", "''")}', 
                    '${tienda.descripcion.replace("'", "''")}', '${tienda.tipo.name}', 
                    $dirStr, $apertura, $cierre, '${tienda.linkWhatsapp ?: ""}', 'ACTIVA', ${tienda.strikes}
                );
            """.trimIndent()
            
            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error guardando tienda remota: ${e.message}", e)
            false
        }
    }
}
