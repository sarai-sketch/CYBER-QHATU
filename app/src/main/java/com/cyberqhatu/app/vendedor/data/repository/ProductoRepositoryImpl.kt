package com.cyberqhatu.app.vendedor.data.repository

import com.cyberqhatu.app.data.local.dao.ProductoDao
import com.cyberqhatu.app.tienda.Producto
import com.cyberqhatu.app.vendedor.data.mapper.ProductoMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación del repositorio de productos conectado a Room DAO.
 */
class ProductoRepositoryImpl(
    private val productoDao: ProductoDao
) : ProductoRepository {

    override suspend fun guardarProducto(
        nombre: String,
        precio: Double,
        descripcion: String,
        categoria: String,
        stock: Int,
        imagenUri: String?
    ) {
        val entity = ProductoMapper.toEntity(
            nombre = nombre,
            precio = precio,
            descripcion = descripcion,
            categoria = categoria,
            stock = stock
        )
        productoDao.insertarProducto(entity)
    }

    override fun obtenerProductos(): Flow<List<Producto>> {
        return productoDao.obtenerTodosLosProductos().map { entities ->
            entities.map { ProductoMapper.toDomain(it) }
        }
    }
}
