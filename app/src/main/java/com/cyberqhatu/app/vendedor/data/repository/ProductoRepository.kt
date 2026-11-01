package com.cyberqhatu.app.vendedor.data.repository

import com.cyberqhatu.app.tienda.Producto
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio de productos, inventario y ventas.
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

    suspend fun eliminarProducto(idProducto: String)

    suspend fun ocultarProducto(idProducto: String, estado: String = "INACTIVO")

    suspend fun registrarEntradaInventario(idProducto: String, cantidad: Int, motivo: String = "REABASTECIMIENTO")

    suspend fun registrarAjusteInventario(idProducto: String, nuevoStock: Int, motivo: String)

    suspend fun registrarVenta(idProducto: String, cantidad: Int, precioUnitario: Double, canalVenta: String = "FISICA"): String

    suspend fun eliminarVenta(idVenta: String)

    fun obtenerProductos(): Flow<List<Producto>>
}
