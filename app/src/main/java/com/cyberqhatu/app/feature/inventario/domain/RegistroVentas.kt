package com.cyberqhatu.app.feature.inventario.domain

class RegistroVentas(
    val idRegistro: String,
    val idVendedor: String,
    var totalIngresos: Double = 0.0
) {
    fun registrarVenta(monto: Double) { TODO() }
}
