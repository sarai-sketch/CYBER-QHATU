package com.cyberqhatu.app.tienda

import java.util.Date

enum class EstadoInvitacion {
    PENDIENTE,
    ACEPTADA,
    RECHAZADA,
    CANCELADA
}

class InvitacionTienda(
    val idInvitacion: String,
    val idTienda: String,
    val correoDestinatario: String,
    val rolAsignado: RolMiembro,
    val codigoInvitacion: String,
    var estado: EstadoInvitacion = EstadoInvitacion.PENDIENTE,
    val fechaExpiracion: Date
) {
    fun enviar() {
        // Lógica para enviar la invitación mediante correo o notificación en la app
    }

    fun aceptar() {
        estado = EstadoInvitacion.ACEPTADA
    }

    fun rechazar() {
        estado = EstadoInvitacion.RECHAZADA
    }

    fun cancelar() {
        estado = EstadoInvitacion.CANCELADA
    }
}