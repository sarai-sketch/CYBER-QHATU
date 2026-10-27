package com.cyberqhatu.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Entidad local Room para persistencia de Productos.
 */
@Entity(tableName = "productos_local")
data class ProductoEntity(
    @PrimaryKey val idProducto: String = UUID.randomUUID().toString(),
    val idTienda: String = "t1",
    val idCategoria: String = "c1",
    val marca: String? = null,
    val modelo: String? = null,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val precioCompra: Double? = null,
    val stock: Int,
    val stockMinimo: Int? = null,
    val estado: String = "ACTIVO",
    val etiquetaPrecioTransparente: Boolean = false,
    val fechaCreacion: Long = System.currentTimeMillis()
)
