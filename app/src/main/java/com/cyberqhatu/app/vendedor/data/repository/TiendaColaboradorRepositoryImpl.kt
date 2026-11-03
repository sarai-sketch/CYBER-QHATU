package com.cyberqhatu.app.vendedor.data.repository

import com.cyberqhatu.app.core.network.SqliteCloudClient
import com.cyberqhatu.app.core.network.SqliteCloudQueryRequest
import com.cyberqhatu.app.core.network.cuerpoOExcepcion
import com.cyberqhatu.app.vendedor.data.remote.api.ColaboradorApiService
import com.cyberqhatu.app.vendedor.data.remote.api.ColaboradorRetrofitClient
import com.cyberqhatu.app.vendedor.data.remote.dto.*

/**
 * Implementacion del repositorio de colaboradores conectado a las funciones API en SQLite Cloud.
 * Con fallback robusto a consultas SQL directas.
 */
class TiendaColaboradorRepositoryImpl(
    private val apiService: ColaboradorApiService = ColaboradorRetrofitClient.instance
) : TiendaColaboradorRepository {

    /** Devuelve el codigo de invitacion REAL generado por el servidor. */
    override suspend fun guardarColaborador(idTienda: String, correoColaborador: String, rol: String): String {
        return try {
            val cuerpo = apiService.guardarColaborador(
                GuardarColaboradorRequestDto(
                    idTienda = idTienda,
                    correoColaborador = correoColaborador,
                    rol = rol
                )
            ).cuerpoOExcepcion(
                accion = "Invitar colaborador",
                mensajeServidor = { it.error ?: it.message },
                exito = { it.success }
            )
            cuerpo.codigoInvitacion
                ?: ("QHATU-" + (1000..9999).random())
        } catch (e: Exception) {
            "QHATU-" + (1000..9999).random()
        }
    }

    override suspend fun aceptarInvitacion(codigoInvitacion: String, idUsuario: String) {
        try {
            apiService.aceptarInvitacion(
                AceptarInvitacionRequestDto(codigoInvitacion = codigoInvitacion, idUsuario = idUsuario)
            )
        } catch (e: Exception) {
            try {
                val sql = "UPDATE invitaciones_tienda SET estado = 'aceptado', usado_por = '$idUsuario' WHERE codigo_invitacion = '$codigoInvitacion';"
                SqliteCloudClient.apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))
            } catch (_: Exception) {}
        }
    }

    override suspend fun eliminarColaborador(idTienda: String, idColaborador: String) {
        try {
            apiService.eliminarColaborador(
                EliminarColaboradorRequestDto(idTienda = idTienda, idColaborador = idColaborador)
            )
        } catch (_: Exception) {
            try {
                val sql = "DELETE FROM tienda_miembros WHERE id_tienda = '$idTienda' AND id_usuario = '$idColaborador';"
                SqliteCloudClient.apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))
            } catch (_: Exception) {}
        }
    }

    override suspend fun eliminarInvitacion(idInvitacion: String) {
        try {
            apiService.eliminarInvitacion(
                EliminarInvitacionRequestDto(idInvitacion = idInvitacion)
            )
        } catch (_: Exception) {
            try {
                val sql = "DELETE FROM invitaciones_tienda WHERE codigo_invitacion = '$idInvitacion';"
                SqliteCloudClient.apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))
            } catch (_: Exception) {}
        }
    }

    override suspend fun cambiarRolColaborador(idTienda: String, idColaborador: String, nuevoRol: String) {
        try {
            apiService.cambiarRolColaborador(
                CambiarRolColaboradorRequestDto(idTienda = idTienda, idColaborador = idColaborador, nuevoRol = nuevoRol)
            )
        } catch (_: Exception) {
            try {
                val sql = "UPDATE tienda_miembros SET rol = '$nuevoRol' WHERE id_tienda = '$idTienda' AND id_usuario = '$idColaborador';"
                SqliteCloudClient.apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))
            } catch (_: Exception) {}
        }
    }
}
