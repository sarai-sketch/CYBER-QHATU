package com.cyberqhatu.app.feature.inventario.domain

class EstadisticasVendedor(
    val idEstadistica: String,
    val idVendedor: String,
    var periodo: PeriodoEstadistica = PeriodoEstadistica.MES,
    var totalVentas: Int = 0,
    var totalComprasReabastecimiento: Double = 0.0,
    var ingresoTotal: Double = 0.0,
    var gananciaEstimada: Double = 0.0,
    var idProductoMasVendido: String? = null
) {
    fun calcular() {
    }

    fun generarReporte(): String {
        return ""
    }
}
