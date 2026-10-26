package com.cyberqhatu.app.feature.tienda.data.repository

import com.cyberqhatu.app.core.network.SqliteCloudClient
import com.cyberqhatu.app.core.network.SqliteCloudQueryRequest
import com.cyberqhatu.app.tienda.EstadoProducto
import com.cyberqhatu.app.tienda.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class SqliteCloudProductoRepository {

    private val apiService = SqliteCloudClient.apiService

    fun obtenerProductosRemotos(): Flow<List<Producto>> = flow {
        try {
            val response = apiService.ejecutarSql(
                SqliteCloudQueryRequest(
                    sql = "SELECT id_producto, id_tienda, id_categoria, nombre, descripcion, precio_bs, stock, estado FROM productos ORDER BY fecha_creacion DESC;"
                )
            )
            if (response.isSuccessful && response.body()?.data != null) {
                val lista = response.body()!!.data!!.mapNotNull { row ->
                    val idProd = row["id_producto"]?.toString() ?: return@mapNotNull null
                    val idTienda = row["id_tienda"]?.toString() ?: "T-101"
                    val idCat = row["id_categoria"]?.toString() ?: "General"
                    val nombre = row["nombre"]?.toString() ?: "Producto"
                    val desc = row["descripcion"]?.toString() ?: ""
                    val precio = (row["precio_bs"] as? Number)?.toDouble() ?: 0.0
                    val stock = (row["stock"] as? Number)?.toInt() ?: 0
                    val estadoStr = row["estado"]?.toString() ?: "ACTIVO"

                    Producto(
                        idProducto = idProd,
                        idTienda = idTienda,
                        idCategoria = idCat,
                        nombre = nombre,
                        descripcion = desc,
                        precio = precio,
                        stock = stock,
                        estado = if (estadoStr == "ACTIVO") EstadoProducto.ACTIVO else EstadoProducto.OCULTO
                    )
                }
                emit(lista)
            } else {
                emit(emptyList())
            }
        } catch (_: Exception) {
            emit(emptyList())
        }
    }

    suspend fun guardarProductoRemoto(producto: Producto): Boolean {
        return try {
            val id = producto.idProducto.ifBlank { UUID.randomUUID().toString() }
            val idTienda = producto.idTienda.ifBlank { "T-101" }
            val idCategoria = producto.idCategoria.ifBlank { "General" }
            val sql = "INSERT OR REPLACE INTO productos (id_producto, id_tienda, id_categoria, nombre, descripcion, precio_bs, stock, estado) VALUES ('$id', '$idTienda', '$idCategoria', '${producto.nombre.replace("'", "''")}', '${producto.descripcion.replace("'", "''")}', ${producto.precio}, ${producto.stock}, '${producto.estado.name}');"

            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))
            response.isSuccessful
        } catch (_: Exception) {
            false
        }
    }

    suspend fun actualizarStockYPrecioRemoto(idProducto: String, nuevoStock: Int, nuevoPrecio: Double, nuevoEstado: EstadoProducto): Boolean {
        return try {
            val sql = "UPDATE productos SET stock = $nuevoStock, precio_bs = $nuevoPrecio, estado = '${nuevoEstado.name}' WHERE id_producto = '$idProducto';"
            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))
            response.isSuccessful
        } catch (_: Exception) {
            false
        }
    }
}
