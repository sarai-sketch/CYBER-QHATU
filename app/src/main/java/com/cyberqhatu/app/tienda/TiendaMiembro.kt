package com.cyberqhatu.app.tienda

import java.util.Date

enum class RolMiembro {
    PROPIETARIO,
    ADMINISTRADOR,
    VENDEDOR
}

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