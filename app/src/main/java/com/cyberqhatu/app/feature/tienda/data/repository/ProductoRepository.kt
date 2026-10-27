package com.cyberqhatu.app.feature.tienda.data.repository

import com.cyberqhatu.app.data.local.dao.ProductoDao
import com.cyberqhatu.app.data.local.entity.MovimientoInventarioEntity
import com.cyberqhatu.app.data.local.entity.ProductoEntity
import com.cyberqhatu.app.tienda.EstadoProducto
import com.cyberqhatu.app.tienda.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class ProductoRepository(
    private val productoDao: ProductoDao
) {

    fun obtenerTodosLosProductos(): Flow<List<Producto>> {
        return productoDao.obtenerTodosLosProductos().map { entities ->
            entities.map { entity ->
                Producto(
                    idProducto = entity.idProducto,
                    idTienda = entity.idTienda,
                    idCategoria = entity.idCategoria,
                    marca = entity.marca,
                    modelo = entity.modelo,
                    nombre = entity.nombre,
                    descripcion = entity.descripcion,
                    precio = entity.precio,
                    precioCompra = entity.precioCompra,
                    stock = entity.stock,
                    stockMinimo = entity.stockMinimo,
                    estado = parseEstadoProducto(entity.estado),
                    etiquetaPrecioTransparente = entity.etiquetaPrecioTransparente
                )
            }
        }
    }

    suspend fun guardarProducto(producto: Producto) {
        val entity = ProductoEntity(
            idProducto = producto.idProducto.ifBlank { UUID.randomUUID().toString() },
            idTienda = producto.idTienda.ifBlank { "T-101" },
            idCategoria = producto.idCategoria.ifBlank { "General" },
            marca = producto.marca,
            modelo = producto.modelo,
            nombre = producto.nombre,
            descripcion = producto.descripcion,
            precio = producto.precio,
            precioCompra = producto.precioCompra,
            stock = producto.stock,
            stockMinimo = producto.stockMinimo,
            estado = producto.estado.name,
            etiquetaPrecioTransparente = producto.etiquetaPrecioTransparente
        )
        productoDao.insertarProducto(entity)
    }

    suspend fun actualizarProducto(idProducto: String, nuevoStock: Int, nuevoPrecio: Double, nuevoEstado: EstadoProducto) {
        productoDao.actualizarProducto(
            idProducto = idProducto,
            nuevoStock = nuevoStock,
            nuevoPrecio = nuevoPrecio,
            nuevoEstado = nuevoEstado.name
        )
    }

    suspend fun registrarVentaRapida(idProducto: String, cantidad: Int, stockActual: Int, precioUnitario: Double) {
        val nuevoStock = (stockActual - cantidad).coerceAtLeast(0)
        val nuevoEstado = if (nuevoStock == 0) EstadoProducto.OCULTO.name else EstadoProducto.ACTIVO.name

        productoDao.actualizarStockYEstado(
            idProducto = idProducto,
            nuevoStock = nuevoStock,
            nuevoEstado = nuevoEstado
        )

        val movimiento = MovimientoInventarioEntity(
            idMovimiento = UUID.randomUUID().toString(),
            idProducto = idProducto,
            tipoMovimiento = "SALIDA_VENTA",
            canalVenta = "RAPIDA",
            cantidad = cantidad,
            precioUnitario = precioUnitario,
            stockResultante = nuevoStock
        )
        productoDao.registrarMovimientoInventario(movimiento)
    }

    private fun parseEstadoProducto(estadoStr: String): EstadoProducto {
        return try {
            EstadoProducto.valueOf(estadoStr)
        } catch (_: Exception) {
            EstadoProducto.ACTIVO
        }
    }
}
