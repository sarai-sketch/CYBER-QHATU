package com.cyberqhatu.app.vendedor.data.mapper

import com.cyberqhatu.app.data.local.entity.ProductoEntity
import com.cyberqhatu.app.tienda.Producto

/**
 * Mapeador para convertir entre ProductoEntity y Producto de dominio.
 */
object ProductoMapper {
    fun toEntity(
        nombre: String,
        precio: Double,
        descripcion: String,
        categoria: String,
        stock: Int
    ): ProductoEntity {
        return ProductoEntity(
            nombre = nombre,
            precio = precio,
            descripcion = descripcion,
            idCategoria = categoria,
            stock = stock
        )
    }

    fun toDomain(entity: ProductoEntity): Producto {
        return Producto(
            idProducto = entity.idProducto,
            idTienda = entity.idTienda,
            idCategoria = entity.idCategoria,
            nombre = entity.nombre,
            descripcion = entity.descripcion,
            precio = entity.precio,
            stock = entity.stock
        )
    }
}
