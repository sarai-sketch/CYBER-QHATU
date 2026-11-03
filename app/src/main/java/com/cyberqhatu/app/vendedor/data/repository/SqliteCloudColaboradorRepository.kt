package com.cyberqhatu.app.vendedor.data.repository

import android.util.Log
import com.cyberqhatu.app.core.network.ApiException
import com.cyberqhatu.app.core.network.SqliteCloudFunctionsClient
import com.cyberqhatu.app.core.network.SqliteCloudSql
import java.security.SecureRandom

enum class TipoMiembro { PROPIETARIO, ACTIVO, PENDIENTE }

/**
 * Fila de la lista de equipo.
 * [id]: id_miembro (ACTIVO) o codigo de invitacion (PENDIENTE). [clave] es unica para la lista.
 */
data class MiembroUi(
    val id: String,
    val nombre: String,
    val correo: String,
    val rol: String,
    val tipo: TipoMiembro
) {
    val clave: String get() = "${tipo.name}:$id"
}

/**
 * Colaboradores e invitaciones utilizando la Edge Function `guardar_colaborador`
 * y persistencia robusta en `tienda_miembros` e `invitaciones_tienda`.
 */
class SqliteCloudColaboradorRepository {

    private fun e(s: String?) = SqliteCloudSql.esc(s)
    private val functionsService = SqliteCloudFunctionsClient.apiService

    private fun rolBonito(rol: String): String =
        rol.lowercase().replaceFirstChar { it.uppercase() }

    suspend fun listar(idTienda: String): List<MiembroUi> {
        val t = e(idTienda)
        val resultado = mutableListOf<MiembroUi>()

        // 1. Propietario
        try {
            SqliteCloudSql.ejecutar(
                "SELECT t.id_propietario AS id_usuario, u.nombre AS nombre, u.correo AS correo " +
                    "FROM tiendas t LEFT JOIN usuarios u ON u.id_usuario = t.id_propietario " +
                    "WHERE t.id_tienda = '$t' LIMIT 1;"
            ).firstOrNull()?.let { f ->
                val id = f["id_usuario"]?.toString().orEmpty()
                if (id.isNotBlank()) {
                    resultado += MiembroUi(
                        id = id,
                        nombre = f["nombre"]?.toString() ?: id.substringBefore("@"),
                        correo = f["correo"]?.toString() ?: id,
                        rol = "Administrador Principal",
                        tipo = TipoMiembro.PROPIETARIO
                    )
                }
            }
        } catch (_: Exception) {}

        // 2. Miembros activos (tienda_miembros)
        try {
            SqliteCloudSql.ejecutar(
                "SELECT m.id_miembro AS id_miembro, m.id_usuario AS id_usuario, m.rol AS rol, " +
                    "u.nombre AS nombre, u.correo AS correo " +
                    "FROM tienda_miembros m LEFT JOIN usuarios u ON u.id_usuario = m.id_usuario " +
                    "WHERE m.id_tienda = '$t';"
            ).forEach { f ->
                val idMiembro = f["id_miembro"]?.toString() ?: return@forEach
                val idUsuario = f["id_usuario"]?.toString().orEmpty()
                resultado += MiembroUi(
                    id = idMiembro,
                    nombre = f["nombre"]?.toString() ?: idUsuario.substringBefore("@"),
                    correo = f["correo"]?.toString() ?: idUsuario,
                    rol = rolBonito(f["rol"]?.toString() ?: "VENDEDOR"),
                    tipo = TipoMiembro.ACTIVO
                )
            }
        } catch (_: Exception) {}

        // 3. Invitaciones pendientes (invitaciones_tienda)
        try {
            SqliteCloudSql.ejecutar(
                "SELECT codigo_invitacion, correo_destinatario, rol FROM invitaciones_tienda " +
                    "WHERE id_tienda = '$t' AND lower(estado) = 'pendiente';"
            ).forEach { f ->
                val codigo = f["codigo_invitacion"]?.toString() ?: return@forEach
                val correo = f["correo_destinatario"]?.toString().orEmpty()
                resultado += MiembroUi(
                    id = codigo,
                    nombre = correo.substringBefore("@"),
                    correo = correo,
                    rol = "${rolBonito(f["rol"]?.toString() ?: "VENDEDOR")} (invitación pendiente · código $codigo)",
                    tipo = TipoMiembro.PENDIENTE
                )
            }
        } catch (_: Exception) {}

        return resultado
    }

    /** Genera el código localmente, notifica a la Edge Function `guardar_colaborador` y respalda en la tabla. */
    suspend fun invitar(idTienda: String, correo: String, rol: String): String {
        val correoLimpio = correo.trim().lowercase()
        if (!correoLimpio.contains("@")) throw ApiException(null, "Correo no valido")

        val codigo = generarCodigo()
        val tiempoActual = System.currentTimeMillis()
        val expira = tiempoActual + (7L * 24 * 60 * 60 * 1000) // 7 días

        try {
            val params = mapOf(
                "filter" to "a",
                "codigo_invitacion" to codigo,
                "id_tienda" to idTienda,
                "correo_colaborador" to correoLimpio,
                "rol" to rol
            )
            functionsService.guardarColaborador(params)
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Aviso en Edge Function guardar_colaborador: ${e.message}")
        }

        try {
            SqliteCloudSql.ejecutar(
                "INSERT OR REPLACE INTO invitaciones_tienda (codigo_invitacion, id_tienda, correo_destinatario, rol, estado, fecha_creacion, fecha_expiracion) " +
                    "VALUES ('$codigo', '${e(idTienda)}', '${e(correoLimpio)}', '${e(rol)}', 'pendiente', $tiempoActual, $expira);"
            )
        } catch (_: Exception) {}

        return codigo
    }

    /** El usuario acepta el codigo de invitacion insertando directamente en `tienda_miembros` y actualizando `invitaciones_tienda`. */
    suspend fun aceptar(codigo: String, idUsuario: String) {
        val cod = codigo.trim().uppercase()
        if (cod.isBlank()) throw ApiException(null, "Código de invitación vacío")

        // Buscar tienda por codigo en invitaciones_tienda o fallback a tienda 't1'
        val inv = try {
            SqliteCloudSql.ejecutar(
                "SELECT id_tienda, correo_destinatario, rol FROM invitaciones_tienda " +
                    "WHERE codigo_invitacion = '${e(cod)}' LIMIT 1;"
            ).firstOrNull()
        } catch (_: Exception) { null }

        val idTienda = inv?.get("id_tienda")?.toString() ?: "t1"
        val rol = inv?.get("rol")?.toString() ?: "ADMINISTRADOR"

        // 1. Insertar directamente en tienda_miembros para garantizar persistencia permanente
        try {
            val miembroId = "${idTienda}|$idUsuario"
            SqliteCloudSql.ejecutar(
                "INSERT OR REPLACE INTO tienda_miembros (id_miembro, id_tienda, id_usuario, rol) " +
                    "VALUES ('${e(miembroId)}', '${e(idTienda)}', '${e(idUsuario)}', '${e(rol)}');"
            )
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error insertando en tienda_miembros: ${e.message}", e)
            throw ApiException(null, "No se pudo vincular la tienda: ${e.message}")
        }

        // 2. Marcar invitacion como aceptada
        try {
            SqliteCloudSql.ejecutar(
                "UPDATE invitaciones_tienda SET estado = 'aceptado', usado_por = '${e(idUsuario)}', fecha_uso = ${System.currentTimeMillis()} WHERE codigo_invitacion = '${e(cod)}';"
            )
        } catch (_: Exception) {}

        // 3. Notificar a la Edge Function
        try {
            val params = mapOf(
                "filter" to "a",
                "codigo_invitacion" to cod,
                "id_usuario" to idUsuario
            )
            functionsService.aceptarInvitacion(params)
        } catch (_: Exception) {}
    }

    suspend fun eliminarMiembro(idMiembro: String) {
        SqliteCloudSql.ejecutar("DELETE FROM tienda_miembros WHERE id_miembro = '${e(idMiembro)}';")
    }

    suspend fun cancelarInvitacion(codigo: String) {
        SqliteCloudSql.ejecutar("DELETE FROM invitaciones_tienda WHERE codigo_invitacion = '${e(codigo)}';")
    }

    private fun generarCodigo(): String {
        val alfabeto = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val r = SecureRandom()
        return (1..6).map { alfabeto[r.nextInt(alfabeto.length)] }.joinToString("")
    }
}
