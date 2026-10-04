package com.cyberqhatu.app.inventario

import java.util.Date

class MovimientoInventario(
    val idMovimiento: String,
    val idProducto: String,
    var tipo: String,
    var cantidad: Int,
    var stockResultante: Int,
    val fecha: Date = Date(),
    var canal: String? = null,
    var precioUnitario: Double = 0.0
) {
    fun registrarEntrada() { TODO() }
    fun registrarVentaManual(canal: String) { TODO() }
    fun ajustarStock() { TODO() }
}
