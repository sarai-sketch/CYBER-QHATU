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
        stock: Int,
        imagenUri: String? = null
    ): ProductoEntity {
        return ProductoEntity(
            nombre = nombre,
            precio = precio,
            descripcion = descripcion,
            categoria = categoria,
            stock = stock,
            imagenUri = imagenUri
        )
    }

    fun toDomain(entity: ProductoEntity): Producto {
        return Producto(
            idProducto = entity.idProducto,
            idTienda = entity.idTienda,
            idCategoria = entity.categoria,
            nombre = entity.nombre,
            descripcion = entity.descripcion,
            precio = entity.precio,
            stock = entity.stock
        )
    }
}
