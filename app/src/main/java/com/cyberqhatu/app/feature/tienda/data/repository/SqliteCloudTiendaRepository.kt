package com.cyberqhatu.app.feature.tienda.data.repository

import android.util.Log
import com.cyberqhatu.app.core.network.SqliteCloudClient
import com.cyberqhatu.app.core.network.SqliteCloudQueryRequest
import com.cyberqhatu.app.core.network.parseAsListOfMaps
import com.cyberqhatu.app.tienda.Tienda
import com.cyberqhatu.app.tienda.TipoTienda
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SqliteCloudTiendaRepository {

    private val apiService = SqliteCloudClient.apiService

    fun obtenerTiendasRemotas(): Flow<List<Tienda>> = flow {
        try {
            val sql = "SELECT id_tienda, id_propietario, nombre, descripcion, tipo, link_whatsapp, estado, strikes FROM tiendas WHERE estado = 'ACTIVA';"
            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))
            if (response.body()?.error != null) {
                Log.e("SqliteCloudError", "SQL Error: ${response.body()?.error}")
                emit(emptyList())
                return@flow
            }
            val dataList = response.body()?.data.parseAsListOfMaps()
            if (response.isSuccessful && dataList.isNotEmpty()) {
                val lista = dataList.mapNotNull { row ->
                    val idTienda = row["id_tienda"]?.toString() ?: return@mapNotNull null
                    val idProp = row["id_propietario"]?.toString() ?: "u1"
                    val nombre = row["nombre"]?.toString() ?: "Tienda"
                    val desc = row["descripcion"]?.toString() ?: ""
                    val tipoStr = row["tipo"]?.toString() ?: "FISICA"
                    val dir = row["direccion"]?.toString() ?: row["direccion_google_maps"]?.toString()
                    val apertura = row["horario_apertura"]?.toString()
                    val cierre = row["horario_cierre"]?.toString()
                    val wa = row["link_whatsapp"]?.toString()
                    val strikes = (row["strikes"] as? Number)?.toInt() ?: 0

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
                        strikes = strikes,
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
            val sql = "SELECT id_tienda, id_propietario, nombre, descripcion, tipo, link_whatsapp, estado, strikes FROM tiendas WHERE id_propietario = '$idPropietario' LIMIT 1;"
            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))

            val dataList = response.body()?.data.parseAsListOfMaps()
            if (response.isSuccessful && dataList.isNotEmpty()) {
                val row = dataList.first()
                val idTienda = row["id_tienda"]?.toString() ?: return null
                val nombre = row["nombre"]?.toString() ?: "Tienda"
                val desc = row["descripcion"]?.toString() ?: ""
                val tipoStr = row["tipo"]?.toString() ?: "FISICA"
                val dir = row["direccion"]?.toString() ?: row["direccion_google_maps"]?.toString()
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
                    strikes = strikes,
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error consultando tienda por propietario: ${e.message}", e)
            null
        }
    }

    suspend fun guardarTiendaRemota(tienda: Tienda): Result<Unit> {
        return try {
            // 1. Asegurarnos de que el usuario exista (sin columna 'rol' que no existe en usuarios)
            val sqlUsuarioInsert = "INSERT OR IGNORE INTO usuarios (id_usuario, nombre, correo, telefono_whatsapp) VALUES ('${tienda.idPropietario}', 'Usuario', '${tienda.idPropietario}', '+5910000000');"
            val respUsuario = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sqlUsuarioInsert))
            if (respUsuario.body()?.error != null) {
                return Result.failure(Exception(respUsuario.body()?.error))
            }
            if (!respUsuario.isSuccessful) {
                val err = respUsuario.errorBody()?.string() ?: "Error usuario"
                return Result.failure(Exception("HTTP ${respUsuario.code()}: $err"))
            }

            // 3. Preparar variables para la Tienda
            val descStr = if (tienda.descripcion.isNotBlank()) "'${tienda.descripcion.replace("'", "''")}'" else "NULL"
            val dirStr = if (!tienda.direccionGoogleMaps.isNullOrBlank()) "'${tienda.direccionGoogleMaps!!.replace("'", "''")}'" else "NULL"
            val aperturaStr = if (!tienda.horarioApertura.isNullOrBlank()) "'${tienda.horarioApertura}'" else "'08:00'"
            val cierreStr = if (!tienda.horarioCierre.isNullOrBlank()) "'${tienda.horarioCierre}'" else "'18:00'"
            val tipoStr = tienda.tipo.name
            val waStr = tienda.linkWhatsapp ?: ""
            val estadoStr = tienda.estado.name

            val sqlTienda = """
                INSERT OR REPLACE INTO tiendas (
                    id_tienda, id_propietario, nombre, descripcion, tipo, 
                    direccion_google_maps, horario_apertura, horario_cierre, 
                    link_whatsapp, strikes, estado
                ) VALUES (
                    '${tienda.idTienda}', '${tienda.idPropietario}', '${tienda.nombre.replace("'", "''")}', 
                    $descStr, '$tipoStr', $dirStr, $aperturaStr, $cierreStr, 
                    '$waStr', ${tienda.strikes}, '$estadoStr'
                );
            """.trimIndent()

            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sqlTienda))

            if (response.body()?.error != null) {
                return Result.failure(Exception(response.body()?.error))
            }
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Sin detalle"
                Result.failure(Exception("HTTP ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error guardando tienda remota: ${e.message}", e)
            Result.failure(e)
        }
    }
}
