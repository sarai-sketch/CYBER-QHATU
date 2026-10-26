package com.cyberqhatu.app.vendedor.data.repository

import com.cyberqhatu.app.tienda.Producto
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio de productos.
 */
interface ProductoRepository {
    suspend fun guardarProducto(
        nombre: String,
        precio: Double,
        descripcion: String,
        categoria: String,
        stock: Int,
        imagenUri: String? = null
    )

    fun obtenerProductos(): Flow<List<Producto>>
}
