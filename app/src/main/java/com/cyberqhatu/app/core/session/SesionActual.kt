package com.cyberqhatu.app.core.session

/**
 * Usuario con sesion iniciada. MainActivity lo actualiza al iniciar/cerrar sesion.
 * El id del usuario en la BD es su correo (usuarios.id_usuario = correo).
 */
object SesionActual {
    @Volatile
    var idUsuario: String? = null
}
