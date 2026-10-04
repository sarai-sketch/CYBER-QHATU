package com.cyberqhatu.app.inventario

class EstadisticasVendedor(
    val idEstadistica: String,
    val idVendedor: String,
    var periodo: String,
    var totalVentas: Int = 0,
    var totalComprasReabastecimiento: Double = 0.0,
    var ingresoTotal: Double = 0.0,
    var gananciaEstimada: Double = 0.0,
    var idProductoMasVendido: String? = null
) {
    fun calcular() { TODO() }
    fun generarReporte(): String { TODO() }
}