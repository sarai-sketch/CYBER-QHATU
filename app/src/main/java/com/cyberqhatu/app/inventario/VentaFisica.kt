package com.cyberqhatu.app.inventario

import java.util.Date

class VentaFisica(
    val idVentaFisica: String,
    val idTienda: String,
    val idProducto: String,
    var cantidad: Int,
    var precioVenta: Double,
    val fecha: Date = Date()
) {
    fun registrar() { TODO() }
}
