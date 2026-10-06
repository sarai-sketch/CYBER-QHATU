package com.cyberqhatu.app.usuarios

import java.util.Date

enum class EstadoVerificacionCI {
    NO_VERIFICADO,
    PENDIENTE,
    VERIFICADO,
    RECHAZADO
}

abstract class Usuario(
    val idUsuario: String,
    var nombre: String,
    var correo: String,
    var telefonoWhatsapp: String,
    var fotoPerfil: String? = null,
    var estadoVerificacionCI: EstadoVerificacionCI = EstadoVerificacionCI.NO_VERIFICADO,
    var ci: String? = null,
    var fotoCI: String? = null,
    var fotoBiometricaFacial: String? = null,
    val fechaRegistro: Date = Date()
) {
    open fun registrarse(): Boolean {
        return true
    }

    open fun solicitarVerificacionVendedor(): Boolean {
        return true
    }

    fun agregarFavoritoProducto(idProducto: String) {
    }

    fun agregarFavoritoTienda(idTienda: String) {
    }
}
