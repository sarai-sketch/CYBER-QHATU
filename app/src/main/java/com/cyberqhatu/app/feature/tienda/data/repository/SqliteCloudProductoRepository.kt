package com.cyberqhatu.app.feature.tienda.data.repository

import android.util.Log
import com.cyberqhatu.app.core.network.SqliteCloudClient
import com.cyberqhatu.app.core.network.SqliteCloudFunctionsClient
import com.cyberqhatu.app.core.network.SqliteCloudQueryRequest
import com.cyberqhatu.app.tienda.EstadoProducto
import com.cyberqhatu.app.tienda.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class SqliteCloudProductoRepository {

    private val apiService = SqliteCloudClient.apiService
    private val functionsService = SqliteCloudFunctionsClient.apiService

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

            val response = functionsService.ejecutarEdgeFunction("obtener_datos_inicio")
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
            Log.e("SqliteCloud", "Error obteniendo productos remotos vía Edge Function: ${e.message}", e)
            emit(emptyList())
        }
    }

    suspend fun guardarProductoRemoto(producto: Producto): Boolean {
        return try {
            asegurarDatosInicialesRemotos()

            val id = producto.idProducto.ifBlank { "p-" + UUID.randomUUID().toString().take(6) }
            val idTienda = if (producto.idTienda.isNotBlank() && producto.idTienda != "T-101") producto.idTienda else "t1"
            val idCategoria = if (producto.idCategoria.isNotBlank() && producto.idCategoria != "General") producto.idCategoria else "c1"

            val params = mapOf(
                "filter" to "a",
                "id_producto" to id,
                "id_tienda" to idTienda,
                "id_categoria" to idCategoria,
                "nombre" to producto.nombre,
                "descripcion" to producto.descripcion,
                "precio" to producto.precio,
                "precio_bs" to producto.precio,
                "precio_compra_bs" to 0.0,
                "stock" to producto.stock,
                "condicion" to "NUEVO",
                "estado" to producto.estado.name,
                "etiqueta_transparente" to 1
            )

            // Compatibilidad Dual: Intenta guardar con la Edge Function original "guardar_producto"
            // y si falla, utiliza "guardar_product"
            var response = functionsService.ejecutarEdgeFunction("guardar_producto", params)
            if (!response.isSuccessful) {
                response = functionsService.ejecutarEdgeFunction("guardar_product", params)
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error al guardar producto remoto: ${e.message}", e)
            false
        }
    }

    suspend fun actualizarStockYPrecioRemoto(idProducto: String, nuevoStock: Int, nuevoPrecio: Double, nuevoEstado: EstadoProducto): Boolean {
        return try {
            val params = mapOf(
                "filter" to "a",
                "id_producto" to idProducto,
                "id_tienda" to "t1",
                "id_categoria" to "c1",
                "nombre" to "Producto $idProducto",
                "descripcion" to "Actualizado",
                "precio" to nuevoPrecio,
                "precio_bs" to nuevoPrecio,
                "precio_compra_bs" to 0.0,
                "stock" to nuevoStock,
                "condicion" to "NUEVO",
                "estado" to nuevoEstado.name,
                "etiqueta_transparente" to 1
            )
            var response = functionsService.ejecutarEdgeFunction("guardar_producto", params)
            if (!response.isSuccessful) {
                response = functionsService.ejecutarEdgeFunction("guardar_product", params)
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error al actualizar stock/precio remoto: ${e.message}", e)
            false
        }
    }
}
