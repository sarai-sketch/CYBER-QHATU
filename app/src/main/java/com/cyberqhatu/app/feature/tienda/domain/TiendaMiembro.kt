package com.cyberqhatu.app.feature.tienda.domain

import java.util.Date

class TiendaMiembro(
    val idMiembro: String,
    val idTienda: String,
    val idUsuario: String,
    var rol: RolMiembro,
    val fechaUnion: Date = Date()
) {
    // Métodos útiles de gestión para miembros de la tienda
    fun actualizarRol(nuevoRol: RolMiembro) {
        rol = nuevoRol
    }
}
