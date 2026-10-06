package com.cyberqhatu.app.inventario

import java.util.Date

class MovimientoInventario(
    val idMovimiento: String,
    val idProducto: String,
    var tipo: TipoMovimiento,
    var canal: CanalVenta? = null,
    var cantidad: Int,
    var precioUnitario: Double = 0.0,
    var stockResultante: Int,
    val fecha: Date = Date()
) {
    fun registrarEntrada() {
    }

    fun registrarVentaManual(canal: CanalVenta) {
    }

    fun ajustarStock() {
    }
}
