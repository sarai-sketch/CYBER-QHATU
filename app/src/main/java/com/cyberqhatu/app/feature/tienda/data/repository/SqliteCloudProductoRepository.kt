package com.cyberqhatu.app.feature.tienda.data.repository

import android.util.Log
import com.cyberqhatu.app.core.network.SqliteCloudClient
import com.cyberqhatu.app.core.network.SqliteCloudQueryRequest
import com.cyberqhatu.app.tienda.EstadoProducto
import com.cyberqhatu.app.tienda.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class SqliteCloudProductoRepository {

    private val apiService = SqliteCloudClient.apiService

    suspend fun asegurarDatosInicialesRemotos() {
        try {
            val sqlUsuario = "INSERT OR IGNORE INTO usuarios (id_usuario, nombre, correo, telefono_whatsapp, rol) VALUES ('u1', 'Don Carlos Mamani', 'carlos@gmail.com', '71234567', 'VENDEDOR');"
            val sqlCategoria = "INSERT OR IGNORE INTO categorias (id_categoria, nombre) VALUES ('c1', 'Ropa y Textiles');"
            val sqlTienda = "INSERT OR IGNORE INTO tiendas (id_tienda, id_propietario, nombre, descripcion, tipo, estado) VALUES ('t1', 'u1', 'Moda Andina', 'Tienda oficial de ropa tradicional', 'FISICA', 'ACTIVA');"

            apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sqlUsuario))
            apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sqlCategoria))
            apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sqlTienda))
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error inicializando datos semilla remotos: ${e.message}", e)
        }
    }

    fun obtenerProductosRemotos(): Flow<List<Producto>> = flow {
        try {
            asegurarDatosInicialesRemotos()

            val response = apiService.ejecutarSql(
                SqliteCloudQueryRequest(
                    sql = "SELECT id_producto, id_tienda, id_categoria, nombre, descripcion, precio_bs, stock, estado FROM productos ORDER BY fecha_creacion DESC;"
                )
            )
            if (response.isSuccessful && response.body()?.data != null) {
                val lista = response.body()!!.data!!.mapNotNull { row ->
                    val idProd = row["id_producto"]?.toString() ?: return@mapNotNull null
                    val idTienda = row["id_tienda"]?.toString() ?: "t1"
                    val idCat = row["id_categoria"]?.toString() ?: "c1"
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
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error obteniendo productos remotos: ${e.message}", e)
            emit(emptyList())
        }
    }

    suspend fun guardarProductoRemoto(producto: Producto): Boolean {
        return try {
            asegurarDatosInicialesRemotos()

            val id = producto.idProducto.ifBlank { "p-" + UUID.randomUUID().toString().take(6) }
            val idTienda = if (producto.idTienda.isNotBlank() && producto.idTienda != "T-101") producto.idTienda else "t1"
            val idCategoria = if (producto.idCategoria.isNotBlank() && producto.idCategoria != "General") producto.idCategoria else "c1"

            val sql = "INSERT OR REPLACE INTO productos (id_producto, id_tienda, id_categoria, nombre, descripcion, precio_bs, stock, estado) VALUES ('$id', '$idTienda', '$idCategoria', '${producto.nombre.replace("'", "''")}', '${producto.descripcion.replace("'", "''")}', ${producto.precio}, ${producto.stock}, '${producto.estado.name}');"

            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error al guardar producto remoto: ${e.message}", e)
            false
        }
    }

    suspend fun actualizarStockYPrecioRemoto(idProducto: String, nuevoStock: Int, nuevoPrecio: Double, nuevoEstado: EstadoProducto): Boolean {
        return try {
            val sql = "UPDATE productos SET stock = $nuevoStock, precio_bs = $nuevoPrecio, estado = '${nuevoEstado.name}' WHERE id_producto = '$idProducto';"
            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error al actualizar stock/precio remoto: ${e.message}", e)
            false
        }
    }
}
