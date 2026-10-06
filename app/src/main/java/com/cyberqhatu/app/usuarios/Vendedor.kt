package com.cyberqhatu.app.usuarios

import java.util.Date

class Vendedor(
    idUsuario: String,
    nombre: String,
    correo: String,
    telefonoWhatsapp: String,
    fotoPerfil: String? = null,
    estadoVerificacionCI: EstadoVerificacionCI = EstadoVerificacionCI.NO_VERIFICADO,
    ci: String? = null,
    fotoCI: String? = null,
    fotoBiometricaFacial: String? = null,
    fechaRegistro: Date = Date(),

    var estadoVerificacion: Boolean = false
) : Usuario(
    idUsuario = idUsuario,
    nombre = nombre,
    correo = correo,
    telefonoWhatsapp = telefonoWhatsapp,
    fotoPerfil = fotoPerfil,
    estadoVerificacionCI = estadoVerificacionCI,
    ci = ci,
    fotoCI = fotoCI,
    fotoBiometricaFacial = fotoBiometricaFacial,
    fechaRegistro = fechaRegistro
) {
    fun crearTienda() {
    }

    fun aceptarInvitacion() {
    }
}
