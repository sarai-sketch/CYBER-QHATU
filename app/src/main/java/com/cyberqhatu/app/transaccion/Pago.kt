package com.cyberqhatu.app.transaccion

import java.util.Date

class Pago(
    val idPago: String,
    val idPedido: String,
    var estado: String,
    val fecha: Date = Date()
) {
    fun registrar() { TODO() }
    fun confirmarPagoRecibido() { TODO() }
    fun consultarEstado(): String { TODO() }
}
