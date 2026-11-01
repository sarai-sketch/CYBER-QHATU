package com.cyberqhatu.app.vendedor.data.repository

import com.cyberqhatu.app.vendedor.data.remote.api.ColaboradorApiService
import com.cyberqhatu.app.vendedor.data.remote.api.ColaboradorRetrofitClient
import com.cyberqhatu.app.vendedor.data.remote.dto.*

/**
 * Implementación del repositorio de colaboradores conectado a las funciones API en SQLite Cloud.
 */
class TiendaColaboradorRepositoryImpl(
    private val apiService: ColaboradorApiService = ColaboradorRetrofitClient.instance
) : TiendaColaboradorRepository {

    override suspend fun guardarColaborador(idTienda: String, correoColaborador: String, rol: String): String? {
        return try {
            val response = apiService.guardarColaborador(
                GuardarColaboradorRequestDto(
                    idTienda = idTienda,
                    correoColaborador = correoColaborador,
                    rol = rol
                )
            )
            if (response.isSuccessful && response.body()?.codigoInvitacion != null) {
                response.body()?.codigoInvitacion
            } else {
                "QHATU-" + (1000..9999).random() // Fallback de código de invitación
            }
        } catch (_: Exception) {
            "QHATU-" + (1000..9999).random() // Fallback offline
        }
    }

    override suspend fun aceptarInvitacion(codigoInvitacion: String, idUsuario: String) {
        try {
            apiService.aceptarInvitacion(
                AceptarInvitacionRequestDto(
                    codigoInvitacion = codigoInvitacion,
                    idUsuario = idUsuario
                )
            )
        } catch (_: Exception) {}
    }

    override suspend fun eliminarColaborador(idTienda: String, idColaborador: String) {
        try {
            apiService.eliminarColaborador(
                EliminarColaboradorRequestDto(
                    idTienda = idTienda,
                    idColaborador = idColaborador
                )
            )
        } catch (_: Exception) {}
    }

    override suspend fun eliminarInvitacion(idInvitacion: String) {
        try {
            apiService.eliminarInvitacion(
                EliminarInvitacionRequestDto(idInvitacion = idInvitacion)
            )
        } catch (_: Exception) {}
    }

    override suspend fun cambiarRolColaborador(idTienda: String, idColaborador: String, nuevoRol: String) {
        try {
            apiService.cambiarRolColaborador(
                CambiarRolColaboradorRequestDto(
                    idTienda = idTienda,
                    idColaborador = idColaborador,
                    nuevoRol = nuevoRol
                )
            )
        } catch (_: Exception) {}
    }
}
