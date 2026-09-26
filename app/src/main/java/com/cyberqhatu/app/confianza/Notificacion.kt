package com.cyberqhatu.app.confianza

class Notificacion(
    val idNotificacion: String,
    val idUsuario: String,
    var tipo: String,
    var contenido: String,
    var leido: Boolean = false
) {
    fun enviarPush() { TODO() }
    fun marcarLeido() { TODO() }
}
