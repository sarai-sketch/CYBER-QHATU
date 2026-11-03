package com.cyberqhatu.app.feature.tienda.data.repository

import android.util.Log
import com.cyberqhatu.app.core.network.SqliteCloudClient
import com.cyberqhatu.app.core.network.SqliteCloudQueryRequest
import com.cyberqhatu.app.core.network.parseAsListOfMaps
import com.cyberqhatu.app.tienda.EstadoProducto
import com.cyberqhatu.app.tienda.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class SqliteCloudProductoRepository {

    private val apiService = SqliteCloudClient.apiService

    fun obtenerProductosRemotos(): Flow<List<Producto>> = flow {
        try {
            val sql = "SELECT id_producto, id_tienda, nombre, descripcion, precio, stock, estado, condicion, id_categoria FROM productos;"
            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))

            val dataList = response.body()?.data.parseAsListOfMaps()
            if (response.isSuccessful && dataList.isNotEmpty()) {
                val lista = dataList.mapNotNull { row ->
                    val idProd = row["id_producto"]?.toString() ?: return@mapNotNull null
                    val idTienda = row["id_tienda"]?.toString() ?: "t1"
                    val idCat = row["id_categoria"]?.toString() ?: "c1"
                    val nombre = row["nombre"]?.toString() ?: "Producto"
                    val desc = row["descripcion"]?.toString() ?: ""
                    val precio = (row["precio"] as? Number)?.toDouble() ?: 0.0
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
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error obteniendo productos remotos: ${e.message}", e)
            emit(emptyList())
        }
    }

    suspend fun guardarProductoRemoto(producto: Producto): Boolean {
        return try {
            val id = producto.idProducto.ifBlank { "p-${UUID.randomUUID().toString().take(8)}" }
            val idTienda = if (producto.idTienda.isNotBlank()) producto.idTienda else "t1"
            val idCategoria = if (producto.idCategoria.isNotBlank()) producto.idCategoria else "c1"
            val nombreStr = producto.nombre.replace("'", "''")
            val descStr = producto.descripcion.replace("'", "''")
            
            val sql = "INSERT OR REPLACE INTO productos (id_producto, id_tienda, nombre, descripcion, precio, stock, estado, condicion, id_categoria) VALUES ('$id', '$idTienda', '$nombreStr', '$descStr', ${producto.precio}, ${producto.stock}, '${producto.estado.name}', 'NUEVO', '$idCategoria');"
            
            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))
            
            if (response.body()?.error != null) {
                Log.e("SqliteCloud", "Error en DB al guardar producto: ${response.body()?.error}")
                return false
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error al guardar producto remoto: ${e.message}", e)
            false
        }
    }

    suspend fun actualizarStockYPrecioRemoto(idProducto: String, nuevoStock: Int, nuevoPrecio: Double, nuevoEstado: EstadoProducto): Boolean {
        return try {
            val sql = "UPDATE productos SET stock = $nuevoStock, precio = $nuevoPrecio, estado = '${nuevoEstado.name}' WHERE id_producto = '$idProducto';"
            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error al actualizar stock/precio remoto: ${e.message}", e)
            false
        }
    }
}
