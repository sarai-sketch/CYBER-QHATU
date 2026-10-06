package com.cyberqhatu.app.confianza

import java.time.LocalDateTime

data class Consulta(
    val idConsulta: String,
    val idComprador: String,
    val idTienda: String,
    val idProducto: String? = null,
    val mensaje: String,
    var respuesta: String? = null,
    val fecha: LocalDateTime = LocalDateTime.now(),
    var estado: EstadoConsulta = EstadoConsulta.PENDIENTE
) {
    fun enviar(): Boolean {
        // Lógica de dominio para enviar la consulta
        return true
    }

    fun responder(respuestaMensaje: String) {
        this.respuesta = respuestaMensaje
        this.estado = EstadoConsulta.RESPONDIDA
    }
}