package com.cyberqhatu.app.inventario

import java.util.Date

enum class TipoMovimiento { ENTRADA, SALIDA_ONLINE, SALIDA_FISICA, AJUSTE, RESERVA, LIBERACION_RESERVA }

class MovimientoInventario(
    val idMovimiento: String,
    val idProducto: String,
    var tipo: TipoMovimiento,
    var cantidad: Int,
    var stockResultante: Int,
    val fecha: Date = Date()
) {
    fun registrarEntrada(cantidad: Int) { TODO() }
    fun registrarSalida(cantidad: Int) { TODO() }
    fun ajustarStock(nuevoStock: Int) { TODO() }
}
