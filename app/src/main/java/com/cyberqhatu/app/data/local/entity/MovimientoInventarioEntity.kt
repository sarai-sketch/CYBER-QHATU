package com.cyberqhatu.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad local Room para registro de movimientos de inventario y ventas.
 */
@Entity(tableName = "movimientos_inventario")
data class MovimientoInventarioEntity(
    @PrimaryKey val idMovimiento: String,
    val idProducto: String,
    val tipoMovimiento: String, // ENTRADA, SALIDA_VENTA, AJUSTE
    val canalVenta: String? = null,
    val cantidad: Int,
    val precioUnitario: Double = 0.0,
    val stockResultante: Int,
    val fecha: Long = System.currentTimeMillis()
)
