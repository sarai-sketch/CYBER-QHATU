package com.cyberqhatu.app.feature.inventario.domain

class EstadisticasVentas(
    val idEstadistica: String,
    val idRegistro: String,
    var idProductoMasVendido: String? = null,
    var totalVentas: Int = 0,
    var ingresoTotal: Double = 0.0,
    var promedioVentas: Double = 0.0,
    var periodo: String,
    var canal: CanalVenta = CanalVenta.COMBINADO
) {
    fun calcular() { TODO() }
}
