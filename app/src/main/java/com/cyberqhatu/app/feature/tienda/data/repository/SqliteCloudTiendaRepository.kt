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

    suspend fun obtenerTiendaPorPropietario(idPropietario: String): Tienda? {
        return try {
            val sql = "SELECT id_tienda, id_propietario, nombre, descripcion, tipo, direccion_google_maps, horario_apertura, horario_cierre, link_whatsapp, estado, strikes FROM tiendas WHERE id_propietario = '$idPropietario' LIMIT 1;"
            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))

            if (response.isSuccessful && !response.body()?.data.isNullOrEmpty()) {
                val row = response.body()!!.data!!.first()
                val idTienda = row["id_tienda"]?.toString() ?: return null
                val nombre = row["nombre"]?.toString() ?: "Tienda"
                val desc = row["descripcion"]?.toString() ?: ""
                val tipoStr = row["tipo"]?.toString() ?: "FISICA"
                val dir = row["direccion_google_maps"]?.toString()
                val apertura = row["horario_apertura"]?.toString()
                val cierre = row["horario_cierre"]?.toString()
                val wa = row["link_whatsapp"]?.toString()
                val strikes = (row["strikes"] as? Double)?.toInt() ?: 0

                val tipoEnum = try { TipoTienda.valueOf(tipoStr) } catch (_: Exception) { TipoTienda.FISICA }

                Tienda(
                    idTienda = idTienda,
                    idPropietario = idPropietario,
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
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error consultando tienda por propietario: ${e.message}", e)
            null
        }
    }

    suspend fun guardarTiendaRemota(tienda: Tienda): Boolean {
        return try {
            // 1. Asegurarnos de que el usuario exista
            val sqlUsuarioInsert = "INSERT OR IGNORE INTO usuarios (id_usuario, nombre, correo, telefono_whatsapp, tipo_rol) VALUES ('${tienda.idPropietario}', 'Usuario', '${tienda.idPropietario}', '+5910000000', 'VENDEDOR');"
            apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sqlUsuarioInsert))

            // 2. Y actualizarlo a VENDEDOR
            val sqlUsuarioUpdate = "UPDATE usuarios SET tipo_rol = 'VENDEDOR' WHERE id_usuario = '${tienda.idPropietario}';"
            apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sqlUsuarioUpdate))

            // 3. Preparar variables para la Tienda
            val descStr = if (tienda.descripcion.isNotBlank()) "'${tienda.descripcion.replace("'", "''")}'" else "NULL"
            val dirStr = if (tienda.direccionGoogleMaps != null && tienda.direccionGoogleMaps!!.isNotBlank()) "'${tienda.direccionGoogleMaps!!.replace("'", "''")}'" else "NULL"
            val apertura = if (tienda.horarioApertura != null && tienda.horarioApertura!!.isNotBlank()) "'${tienda.horarioApertura}'" else "NULL"
            val cierre = if (tienda.horarioCierre != null && tienda.horarioCierre!!.isNotBlank()) "'${tienda.horarioCierre}'" else "NULL"
            
            // Reemplazar saltos de línea y formatear para SQLite a 1 sola línea para evitar parseos erróneos.
            val sqlTienda = "INSERT OR REPLACE INTO tiendas (id_tienda, id_propietario, nombre, descripcion, logo, foto_portada, tipo, direccion_google_maps, horario_apertura, horario_cierre, link_whatsapp, qr_fijo_tienda, strikes, estado) VALUES ('${tienda.idTienda}', '${tienda.idPropietario}', '${tienda.nombre.replace("'", "''")}', $descStr, NULL, NULL, 'FISICA', $dirStr, $apertura, $cierre, '${tienda.linkWhatsapp ?: ""}', NULL, ${tienda.strikes}, 'ACTIVA');"
            
            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sqlTienda))
            
            if (response.body()?.error != null) {
                Log.e("SqliteCloud", "Error en Payload de SQLite: ${response.body()?.error}")
                return false
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error guardando tienda remota: ${e.message}", e)
            false
        }
    }
}
