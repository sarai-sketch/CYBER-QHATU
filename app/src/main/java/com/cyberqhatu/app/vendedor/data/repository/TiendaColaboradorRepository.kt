package com.cyberqhatu.app.vendedor.data.repository

/**
 * Interfaz de repositorio para gestión de colaboradores e invitaciones de tienda.
 */
interface TiendaColaboradorRepository {
    /** Devuelve el codigo real del servidor; lanza ApiException si falla. */
    suspend fun guardarColaborador(idTienda: String, correoColaborador: String, rol: String): String
    suspend fun aceptarInvitacion(codigoInvitacion: String, idUsuario: String)
    suspend fun eliminarColaborador(idTienda: String, idColaborador: String)
    suspend fun eliminarInvitacion(idInvitacion: String)
    suspend fun cambiarRolColaborador(idTienda: String, idColaborador: String, nuevoRol: String)
}
