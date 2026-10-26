package com.cyberqhatu.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Entidad de Room para almacenar los productos registrados por los vendedores.
 */
@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey val idProducto: String = UUID.randomUUID().toString(),
    val idTienda: String = "t1",
    val nombre: String,
    val precio: Double,
    val descripcion: String,
    val categoria: String,
    val stock: Int,
    val imagenUri: String? = null,
    val fechaCreacion: Long = System.currentTimeMillis()
)
