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
 * Colaboradores e invitaciones de una tienda utilizando la Edge Function `guardar_colaborador`
 * con generación de código local para evitar bloqueos de CloudSync.
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

    /** Genera el código localmente, notifica a la Edge Function `guardar_colaborador` y devuelve el código de 6 caracteres. */
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
                "correo_destinatario" to correoLimpio,
                "rol" to rol,
                "estado" to "pendiente",
                "fecha_creacion" to tiempoActual.toString(),
                "fecha_expiracion" to expira.toString()
            )
            functionsService.guardarColaborador(params)
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Aviso en Edge Function guardar_colaborador: ${e.message}")
        }

        return codigo
    }

    /** El usuario acepta el codigo de invitacion. */
    suspend fun aceptar(codigo: String, idUsuario: String) {
        val cod = codigo.trim().uppercase()
        val inv = SqliteCloudSql.ejecutar(
            "SELECT id_tienda, correo_destinatario, rol FROM invitaciones_tienda " +
                "WHERE codigo_invitacion = '${e(cod)}' AND lower(estado) = 'pendiente' LIMIT 1;"
        ).firstOrNull() ?: throw ApiException(null, "Código inválido o ya utilizado")

        val correoInvitado = inv["correo_destinatario"]?.toString().orEmpty()
        if (correoInvitado.isNotBlank() && !correoInvitado.equals(idUsuario, ignoreCase = true)) {
            throw ApiException(null, "Este código fue emitido para otro correo")
        }
        val idTienda = inv["id_tienda"]?.toString() ?: throw ApiException(null, "Invitación sin tienda")
        val rol = inv["rol"]?.toString() ?: "VENDEDOR"

        val yaMiembro = SqliteCloudSql.ejecutar(
            "SELECT id_miembro FROM tienda_miembros WHERE id_tienda = '${e(idTienda)}' " +
                "AND id_usuario = '${e(idUsuario)}' LIMIT 1;"
        )
        if (yaMiembro.isEmpty()) {
            SqliteCloudSql.ejecutar(
                "INSERT INTO tienda_miembros (id_miembro, id_tienda, id_usuario, rol) " +
                    "VALUES ('${e(idTienda)}|${e(idUsuario)}', '${e(idTienda)}', '${e(idUsuario)}', '${e(rol)}');"
            )
        }
        SqliteCloudSql.ejecutar(
            "UPDATE invitaciones_tienda SET estado = 'aceptado', usado_por = '${e(idUsuario)}', fecha_uso = ${System.currentTimeMillis()} WHERE codigo_invitacion = '${e(cod)}';"
        )
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
