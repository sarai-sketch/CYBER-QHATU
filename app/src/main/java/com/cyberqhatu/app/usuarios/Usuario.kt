package com.cyberqhatu.app.usuarios

import java.util.Date

abstract class Usuario(
    val idUsuario: String,
    var nombre: String,
    var correo: String,
    var telefonoWhatsapp: String,
    var fotoPerfil: String?,
    var estadoVerificacionCI: Boolean,
    var ci: String,
    var fotoCI: String?,
    var fotoBiometricaFacial: String?,
    val fechaRegistro: Date = Date()
) {
    open fun registrarse(): Boolean {
        // Lógica para registrar al usuario en la base de datos
        return true
    }

    open fun solicitarVerificacionVendedor(): Boolean {
        // Lógica para enviar la documentación de verificación al moderador
        return true
    }

    fun agregarFavoritoProducto(idProducto: String) {
        // Lógica para agregar un producto a la lista de favoritos
    }

    fun agregarFavoritoTienda(idTienda: String) {
        // Lógica para agregar una tienda a la lista de favoritos
    }
}