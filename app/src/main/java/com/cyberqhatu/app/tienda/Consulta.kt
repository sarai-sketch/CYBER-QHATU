package com.cyberqhatu.app.tienda

import java.util.Date

class Consulta(
    val idConsulta: String,
    val idComprador: String,
    val idTienda: String,
    val idProducto: String? = null,
    var idRespuestaUsada: String? = null,
    var mensaje: String,
    var respuesta: String? = null,
    val fecha: Date = Date(),
    var estado: EstadoConsulta = EstadoConsulta.PENDIENTE
) {
    fun enviar() { TODO() }
    fun responder(texto: String) { TODO() }
}
