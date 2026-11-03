package com.cyberqhatu.app.feature.tienda.data.repository

import android.util.Log
import com.cyberqhatu.app.core.network.SqliteCloudClient
import com.cyberqhatu.app.core.network.SqliteCloudFunctionsClient
import com.cyberqhatu.app.core.network.SqliteCloudQueryRequest
import com.cyberqhatu.app.core.network.SqliteCloudSql
import com.cyberqhatu.app.core.network.parseAsListOfMaps
import com.cyberqhatu.app.tienda.EstadoTienda
import com.cyberqhatu.app.tienda.Tienda
import com.cyberqhatu.app.tienda.TipoTienda
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SqliteCloudTiendaRepository {

    private val apiService = SqliteCloudClient.apiService
    private val functionsService = SqliteCloudFunctionsClient.apiService

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

    /**
     * Tienda del usuario: verifica si es propietario o si pertenece a `tienda_miembros`.
     * Si la tienda no existe en la tabla `tiendas` pero está en `tienda_miembros`, genera una tienda por defecto para que no falle.
     */
    suspend fun obtenerTiendaDeUsuario(idUsuario: String): Tienda? {
        return try {
            val u = SqliteCloudSql.esc(idUsuario)
            val sql = "SELECT t.id_tienda, t.id_propietario, t.nombre, t.descripcion, t.tipo, t.link_whatsapp, t.estado, t.strikes FROM tiendas t JOIN tienda_miembros m ON t.id_tienda = m.id_tienda WHERE m.id_usuario = '$u' OR t.id_propietario = '$u' LIMIT 1;"
            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))
            val dataList = response.body()?.data.parseAsListOfMaps()

            if (response.isSuccessful && dataList.isNotEmpty()) {
                val row = dataList.first()
                val idTienda = row["id_tienda"]?.toString() ?: "t1"
                val idProp = row["id_propietario"]?.toString() ?: idUsuario
                val nombre = row["nombre"]?.toString() ?: "Tienda Colaboradora"
                val desc = row["descripcion"]?.toString() ?: ""
                val tipoStr = row["tipo"]?.toString() ?: "FISICA"
                val wa = row["link_whatsapp"]?.toString() ?: "https://wa.me/59170000000"
                val strikes = (row["strikes"] as? Number)?.toInt() ?: 0
                val tipoEnum = try { TipoTienda.valueOf(tipoStr) } catch (_: Exception) { TipoTienda.FISICA }

                Tienda(
                    idTienda = idTienda,
                    idPropietario = idProp,
                    nombre = nombre,
                    descripcion = desc,
                    logo = null,
                    tipo = tipoEnum,
                    linkWhatsapp = wa,
                    strikes = strikes,
                    estado = EstadoTienda.ACTIVA
                )
            } else {
                // Fallback: verificar si al menos tiene registro en tienda_miembros aunque la tabla tiendas no haga join
                val miembroRow = SqliteCloudSql.ejecutar(
                    "SELECT id_tienda, rol FROM tienda_miembros WHERE id_usuario = '$u' LIMIT 1;"
                ).firstOrNull()

                if (miembroRow != null) {
                    val idT = miembroRow["id_tienda"]?.toString() ?: "t1"
                    Tienda(
                        idTienda = idT,
                        idPropietario = "u1",
                        nombre = "Tienda Colaborativa ($idT)",
                        descripcion = "Puesto administrado en equipo",
                        logo = null,
                        tipo = TipoTienda.FISICA,
                        linkWhatsapp = "https://wa.me/59170000000",
                        estado = EstadoTienda.ACTIVA
                    )
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error buscando tienda del usuario: ${e.message}", e)
            null
        }
    }

    suspend fun guardarTiendaRemota(tienda: Tienda): Result<Unit> {
        return try {
            val params = mapOf(
                "filter" to "a",
                "id_tienda" to tienda.idTienda,
                "id_propietario" to tienda.idPropietario,
                "nombre" to tienda.nombre,
                "descripcion" to tienda.descripcion,
                "tipo" to tienda.tipo.name,
                "direccion_google_maps" to (tienda.direccionGoogleMaps ?: ""),
                "horario_apertura" to (tienda.horarioApertura ?: "08:00"),
                "horario_cierre" to (tienda.horarioCierre ?: "18:00"),
                "link_whatsapp" to (tienda.linkWhatsapp ?: ""),
                "estado" to tienda.estado.name
            )

            // Intentar vía Edge Function guardar_tienda
            val response = functionsService.ejecutarEdgeFunction("guardar_tienda", params)
            
            // Asegurar que el creador/propietario también esté en tienda_miembros
            try {
                val miembroId = "${tienda.idTienda}|${tienda.idPropietario}"
                val sqlMiembro = "INSERT OR REPLACE INTO tienda_miembros (id_miembro, id_tienda, id_usuario, rol) VALUES ('$miembroId', '${tienda.idTienda}', '${tienda.idPropietario}', 'PROPIETARIO');"
                apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sqlMiembro))
            } catch (_: Exception) {}

            if (response.isSuccessful && response.body()?.error == null) {
                return Result.success(Unit)
            }

            // Fallback SQL directo si la Edge Function falla
            val sqlUsuarioInsert = "INSERT OR IGNORE INTO usuarios (id_usuario, nombre, correo, telefono_whatsapp) VALUES ('${tienda.idPropietario}', 'Usuario', '${tienda.idPropietario}', '+5910000000');"
            apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sqlUsuarioInsert))

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

            val sqlResponse = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sqlTienda))
            if (sqlResponse.isSuccessful && sqlResponse.body()?.error == null) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(sqlResponse.body()?.error ?: "Error guardando tienda SQL"))
            }
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error guardando tienda remota: ${e.message}", e)
            Result.failure(e)
        }
    }
}
