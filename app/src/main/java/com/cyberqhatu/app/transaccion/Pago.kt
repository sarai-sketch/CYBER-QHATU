package com.cyberqhatu.app.transaccion

import java.util.Date

enum class EstadoPago { PENDIENTE, PAGADO, CONFIRMADO }

class Pago(
    val idPago: String,
    val idPedido: String,
    var metodo: MetodoPago,
    var estado: EstadoPago = EstadoPago.PENDIENTE,
    val fecha: Date = Date()
) {
    fun registrar() { TODO() }
    fun confirmarPagoRecibido() { TODO() }
    fun consultarEstado(): EstadoPago { TODO() }
}
