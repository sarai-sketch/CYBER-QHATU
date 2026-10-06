package com.cyberqhatu.app.confianza

import java.util.Date

class Consulta(
    val idConsulta: String,
    val idComprador: String,
    val idTienda: String,
    val idProducto: String? = null,
    val mensaje: String,
    var respuesta: String? = null,
    val fecha: Date = Date(),
    var estado: EstadoConsulta = EstadoConsulta.PENDIENTE
) {
    fun enviar(): Boolean {
        return true
    }

    fun responder(respuestaMensaje: String) {
        this.respuesta = respuestaMensaje
        this.estado = EstadoConsulta.RESPONDIDA
    }
}
