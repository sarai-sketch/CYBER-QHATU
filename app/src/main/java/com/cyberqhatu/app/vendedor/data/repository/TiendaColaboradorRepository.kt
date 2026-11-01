package com.cyberqhatu.app.vendedor.data.repository

/**
 * Interfaz de repositorio para gestión de colaboradores e invitaciones de tienda.
 */
interface TiendaColaboradorRepository {
    suspend fun guardarColaborador(idTienda: String = "t1", correoColaborador: String, rol: String = "ADMINISTRADOR_COLABORADOR"): String?
    suspend fun aceptarInvitacion(codigoInvitacion: String, idUsuario: String)
    suspend fun eliminarColaborador(idTienda: String = "t1", idColaborador: String)
    suspend fun eliminarInvitacion(idInvitacion: String)
    suspend fun cambiarRolColaborador(idTienda: String = "t1", idColaborador: String, nuevoRol: String)
}
