package com.cyberqhatu.app.vendedor.data.repository

import com.cyberqhatu.app.data.local.dao.ProductoDao
import com.cyberqhatu.app.data.local.entity.MovimientoInventarioEntity
import com.cyberqhatu.app.tienda.Producto
import com.cyberqhatu.app.vendedor.data.mapper.ProductoMapper
import com.cyberqhatu.app.vendedor.data.remote.api.ProductoApiService
import com.cyberqhatu.app.vendedor.data.remote.api.ProductoRetrofitClient
import com.cyberqhatu.app.vendedor.data.remote.dto.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Implementación del repositorio de productos con arquitectura Offline-First:
 * Persiste en la base de datos local (Room) y sincroniza con las funciones API de SQLite Cloud.
 */
class ProductoRepositoryImpl(
    private val productoDao: ProductoDao,
    private val apiService: ProductoApiService = ProductoRetrofitClient.instance
) : ProductoRepository {

    override suspend fun guardarProducto(
        nombre: String,
        precio: Double,
        descripcion: String,
        categoria: String,
        stock: Int,
        imagenUri: String?
    ) {
        // 1. Guardar localmente en Room DB
        val entity = ProductoMapper.toEntity(
            nombre = nombre,
            precio = precio,
            descripcion = descripcion,
            categoria = categoria,
            stock = stock
        )
        productoDao.insertarProducto(entity)

        // 2. Sincronizar con API SQLite Cloud (https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/guardar_producto)
        try {
            val requestDto = GuardarProductoRequestDto(
                idProducto = entity.idProducto,
                idTienda = entity.idTienda,
                idCategoria = entity.idCategoria,
                nombre = nombre,
                descripcion = descripcion,
                precio = precio,
                stock = stock
            )
            apiService.guardarProducto(requestDto)
        } catch (_: Exception) {
            // Modo offline
        }
    }

    override suspend fun eliminarProducto(idProducto: String) {
        // 1. Eliminar localmente en Room DB
        productoDao.eliminarProducto(idProducto)

        // 2. Sincronizar eliminación en SQLite Cloud (https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/eliminar_producto)
        try {
            apiService.eliminarProducto(EliminarProductoRequestDto(idProducto = idProducto))
        } catch (_: Exception) {
            // Modo offline
        }
    }

    override suspend fun ocultarProducto(idProducto: String, estado: String) {
        // 1. Actualizar estado local en Room DB
        productoDao.actualizarStockYEstado(idProducto = idProducto, nuevoStock = 0, nuevoEstado = estado)

        // 2. Sincronizar en SQLite Cloud (https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/ocultar_producto)
        try {
            apiService.ocultarProducto(OcultarProductoRequestDto(idProducto = idProducto, estado = estado))
        } catch (_: Exception) {
            // Modo offline
        }
    }

    override suspend fun registrarEntradaInventario(idProducto: String, cantidad: Int, motivo: String) {
        // 1. Registrar movimiento localmente
        val productoActual = productoDao.obtenerProductoPorId(idProducto)
        val nuevoStock = (productoActual?.stock ?: 0) + cantidad
        if (productoActual != null) {
            productoDao.actualizarStockYEstado(idProducto = idProducto, nuevoStock = nuevoStock, nuevoEstado = productoActual.estado)
        }
        productoDao.registrarMovimientoInventario(
            MovimientoInventarioEntity(
                idMovimiento = UUID.randomUUID().toString(),
                idProducto = idProducto,
                tipoMovimiento = "ENTRADA",
                cantidad = cantidad,
                precioUnitario = productoActual?.precio ?: 0.0,
                stockResultante = nuevoStock
            )
        )

        // 2. Sincronizar en SQLite Cloud (https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/guardar_entrada_inventario)
        try {
            apiService.guardarEntradaInventario(
                EntradaInventarioRequestDto(
                    idProducto = idProducto,
                    cantidad = cantidad,
                    motivo = motivo
                )
            )
        } catch (_: Exception) {
            // Modo offline
        }
    }

    override suspend fun registrarAjusteInventario(idProducto: String, nuevoStock: Int, motivo: String) {
        // 1. Actualizar stock localmente
        val productoActual = productoDao.obtenerProductoPorId(idProducto)
        if (productoActual != null) {
            productoDao.actualizarStockYEstado(idProducto = idProducto, nuevoStock = nuevoStock, nuevoEstado = productoActual.estado)
        }
        productoDao.registrarMovimientoInventario(
            MovimientoInventarioEntity(
                idMovimiento = UUID.randomUUID().toString(),
                idProducto = idProducto,
                tipoMovimiento = "AJUSTE",
                cantidad = nuevoStock,
                precioUnitario = productoActual?.precio ?: 0.0,
                stockResultante = nuevoStock
            )
        )

        // 2. Sincronizar en SQLite Cloud (https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/guardar_ajuste_inventario)
        try {
            apiService.guardarAjusteInventario(
                AjusteInventarioRequestDto(
                    idProducto = idProducto,
                    nuevoStock = nuevoStock,
                    motivo = motivo
                )
            )
        } catch (_: Exception) {
            // Modo offline
        }
    }

    override suspend fun registrarVenta(idProducto: String, cantidad: Int, precioUnitario: Double, canalVenta: String): String {
        val idVentaGenerada = UUID.randomUUID().toString()

        // 1. Registrar salida por venta en Room DB
        val productoActual = productoDao.obtenerProductoPorId(idProducto)
        val nuevoStock = maxOf(0, (productoActual?.stock ?: 0) - cantidad)
        if (productoActual != null) {
            productoDao.actualizarStockYEstado(idProducto = idProducto, nuevoStock = nuevoStock, nuevoEstado = productoActual.estado)
        }
        productoDao.registrarMovimientoInventario(
            MovimientoInventarioEntity(
                idMovimiento = idVentaGenerada,
                idProducto = idProducto,
                tipoMovimiento = "SALIDA_VENTA",
                canalVenta = canalVenta,
                cantidad = cantidad,
                precioUnitario = precioUnitario,
                stockResultante = nuevoStock
            )
        )

        // 2. Sincronizar en SQLite Cloud (https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/guardar_venta)
        try {
            apiService.guardarVenta(
                GuardarVentaRequestDto(
                    idVenta = idVentaGenerada,
                    idProducto = idProducto,
                    cantidad = cantidad,
                    precioUnitario = precioUnitario,
                    canalVenta = canalVenta
                )
            )
        } catch (_: Exception) {
            // Modo offline
        }

        return idVentaGenerada
    }

    override suspend fun eliminarVenta(idVenta: String) {
        // 1. Eliminar movimiento en SQLite Cloud (https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/eliminar_venta)
        try {
            apiService.eliminarVenta(
                EliminarVentaRequestDto(idVenta = idVenta)
            )
        } catch (_: Exception) {
            // Modo offline
        }
    }

    override fun obtenerProductos(): Flow<List<Producto>> {
        return productoDao.obtenerTodosLosProductos().map { entities ->
            entities.map { ProductoMapper.toDomain(it) }
        }
    }
}
