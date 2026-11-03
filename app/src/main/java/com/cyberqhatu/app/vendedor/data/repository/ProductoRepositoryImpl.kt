package com.cyberqhatu.app.vendedor.data.repository

import android.util.Log
import com.cyberqhatu.app.core.network.cuerpoOExcepcion
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

    private companion object {
        const val TAG = "ProductoRepository"
    }

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
                .cuerpoOExcepcion("Guardar producto", { it.error ?: it.message }, { it.success })
        } catch (e: Exception) {
            Log.e(TAG, "No se pudo sincronizar guardar_producto (queda solo en local)", e)
        }
    }

    override suspend fun eliminarProducto(idProducto: String) {
        // 1. Eliminar localmente en Room DB
        productoDao.eliminarProducto(idProducto)

        // 2. Sincronizar eliminación en SQLite Cloud (https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/eliminar_producto)
        try {
            apiService.eliminarProducto(EliminarProductoRequestDto(idProducto = idProducto))
                .cuerpoOExcepcion("Eliminar producto", { it.error ?: it.message }, { it.success })
        } catch (e: Exception) {
            Log.e(TAG, "No se pudo sincronizar eliminar_producto", e)
        }
    }

    override suspend fun ocultarProducto(idProducto: String, estado: String) {
        // 1. Actualizar estado local en Room DB
        productoDao.actualizarStockYEstado(idProducto = idProducto, nuevoStock = 0, nuevoEstado = estado)

        // 2. Sincronizar en SQLite Cloud (https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/ocultar_producto)
        try {
            apiService.ocultarProducto(OcultarProductoRequestDto(idProducto = idProducto, estado = estado))
                .cuerpoOExcepcion("Ocultar producto", { it.error ?: it.message }, { it.success })
        } catch (e: Exception) {
            Log.e(TAG, "No se pudo sincronizar ocultar_producto", e)
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
            val idTienda = productoActual?.idTienda
                ?: throw IllegalStateException("Producto $idProducto no existe en local; no se conoce su tienda")
            apiService.guardarEntradaInventario(
                EntradaInventarioRequestDto(
                    idProducto = idProducto,
                    idTienda = idTienda,
                    cantidad = cantidad,
                    motivo = motivo
                )
            ).cuerpoOExcepcion("Entrada de inventario", { it.error ?: it.message }, { it.success })
        } catch (e: Exception) {
            Log.e(TAG, "No se pudo sincronizar guardar_entrada_inventario", e)
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
            val idTienda = productoActual?.idTienda
                ?: throw IllegalStateException("Producto $idProducto no existe en local; no se conoce su tienda")
            apiService.guardarAjusteInventario(
                AjusteInventarioRequestDto(
                    idProducto = idProducto,
                    idTienda = idTienda,
                    nuevoStock = nuevoStock,
                    motivo = motivo
                )
            ).cuerpoOExcepcion("Ajuste de inventario", { it.error ?: it.message }, { it.success })
        } catch (e: Exception) {
            Log.e(TAG, "No se pudo sincronizar guardar_ajuste_inventario", e)
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
            val idTienda = productoActual?.idTienda
                ?: throw IllegalStateException("Producto $idProducto no existe en local; no se conoce su tienda")
            apiService.guardarVenta(
                GuardarVentaRequestDto(
                    idVenta = idVentaGenerada,
                    idTienda = idTienda,
                    idProducto = idProducto,
                    cantidad = cantidad,
                    precioUnitario = precioUnitario,
                    canalVenta = canalVenta
                )
            ).cuerpoOExcepcion("Guardar venta", { it.error ?: it.message }, { it.success })
        } catch (e: Exception) {
            Log.e(TAG, "No se pudo sincronizar guardar_venta", e)
        }

        return idVentaGenerada
    }

    override suspend fun eliminarVenta(idVenta: String) {
        // 1. Eliminar movimiento en SQLite Cloud (https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/eliminar_venta)
        try {
            apiService.eliminarVenta(
                EliminarVentaRequestDto(idVenta = idVenta)
            ).cuerpoOExcepcion("Eliminar venta", { it.error ?: it.message }, { it.success })
        } catch (e: Exception) {
            Log.e(TAG, "No se pudo sincronizar eliminar_venta", e)
        }
    }

    override fun obtenerProductos(): Flow<List<Producto>> {
        return productoDao.obtenerTodosLosProductos().map { entities ->
            entities.map { ProductoMapper.toDomain(it) }
        }
    }
}
