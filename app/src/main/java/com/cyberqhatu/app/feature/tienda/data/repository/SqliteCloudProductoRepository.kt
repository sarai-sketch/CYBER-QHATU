package com.cyberqhatu.app.feature.tienda.data.repository

import android.util.Log
import com.cyberqhatu.app.core.network.ApiException
import com.cyberqhatu.app.core.network.SqliteCloudSql
import com.cyberqhatu.app.tienda.EstadoProducto
import com.cyberqhatu.app.tienda.Producto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Operaciones CRUD de productos contra SQLite Cloud con SQL directo.
 * Lanza [ApiException] si una operacion obligatoria falla.
 */
class SqliteCloudProductoRepository {

    private fun e(s: String?) = SqliteCloudSql.esc(s)

    private fun filaAProducto(row: Map<String, Any?>): Producto? {
        val idProd = row["id_producto"]?.toString() ?: return null
        val estadoStr = row["estado"]?.toString() ?: "ACTIVO"
        return Producto(
            idProducto = idProd,
            idTienda = row["id_tienda"]?.toString() ?: "t1",
            idCategoria = row["id_categoria"]?.toString() ?: "c1",
            nombre = row["nombre"]?.toString() ?: "Producto",
            descripcion = row["descripcion"]?.toString() ?: "",
            precio = row["precio"]?.toString()?.toDoubleOrNull() ?: 0.0,
            stock = row["stock"]?.toString()?.toDoubleOrNull()?.toInt() ?: 0,
            estado = if (estadoStr == "ACTIVO") EstadoProducto.ACTIVO else EstadoProducto.OCULTO
        )
    }

    // ---------- Lecturas ----------

    /** Todos los productos (catalogo de Home y perfiles). */
    fun obtenerProductosRemotos(): Flow<List<Producto>> = flow {
        val lista = try {
            SqliteCloudSql.ejecutar("SELECT * FROM productos ORDER BY rowid DESC;").mapNotNull { filaAProducto(it) }
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            Log.e("SqliteCloud", "Error obteniendo productos remotos: ${ex.message}", ex)
            emptyList()
        }
        emit(lista)
    }

    /** Productos de UNA tienda (inventario). Incluye productos de la tienda específica o genéricos. */
    suspend fun obtenerProductosDeTienda(idTienda: String): List<Producto> {
        val t = e(idTienda)
        val sql = if (idTienda.isBlank()) {
            "SELECT * FROM productos ORDER BY rowid DESC;"
        } else {
            "SELECT * FROM productos WHERE id_tienda = '$t' OR id_tienda = '' OR id_tienda = 't1' ORDER BY rowid DESC;"
        }
        return try {
            SqliteCloudSql.ejecutar(sql).mapNotNull { filaAProducto(it) }
        } catch (ex: Exception) {
            Log.e("SqliteCloud", "Error en obtenerProductosDeTienda: ${ex.message}")
            emptyList()
        }
    }

    /** Ingresos y numero de ventas del mes actual de la tienda. */
    suspend fun estadisticasMes(idTienda: String): Pair<Double, Int> {
        val t = e(idTienda)
        return try {
            val fila = SqliteCloudSql.ejecutar(
                "SELECT COALESCE(SUM(cantidad * precio_unitario), 0) AS ingresos, COUNT(*) AS ventas " +
                    "FROM ventas_registro WHERE id_tienda = '$t' OR id_tienda = 't1' OR id_tienda = '' " +
                    "AND fecha >= (strftime('%s', 'now', 'start of month') * 1000);"
            ).firstOrNull() ?: return 0.0 to 0
            val ingresos = fila["ingresos"]?.toString()?.toDoubleOrNull() ?: 0.0
            val ventas = fila["ventas"]?.toString()?.toDoubleOrNull()?.toInt() ?: 0
            ingresos to ventas
        } catch (_: Exception) {
            0.0 to 0
        }
    }

    // ---------- Escrituras ----------

    /** Inserta/reemplaza un producto. Lanza ApiException si falla. */
    suspend fun guardarProducto(producto: Producto) {
        val idTiendaFinal = producto.idTienda.ifBlank { "t1" }
        val id = producto.idProducto.ifBlank { "p-${System.currentTimeMillis()}" }
        val valoresBase = "'${e(id)}', '${e(idTiendaFinal)}', '${e(producto.nombre)}', " +
            "'${e(producto.descripcion)}', ${producto.precio}, ${producto.stock}, '${producto.estado.name}', 'NUEVO'"
        try {
            SqliteCloudSql.ejecutar(
                "INSERT OR REPLACE INTO productos " +
                    "(id_producto, id_tienda, nombre, descripcion, precio, stock, estado, condicion, id_categoria) " +
                    "VALUES ($valoresBase, '${e(producto.idCategoria)}');"
            )
        } catch (ex: Exception) {
            SqliteCloudSql.ejecutar(
                "INSERT OR REPLACE INTO productos " +
                    "(id_producto, id_tienda, nombre, descripcion, precio, stock, estado, condicion) " +
                    "VALUES ($valoresBase);"
            )
        }
    }

    suspend fun actualizarStockYPrecioRemoto(
        idProducto: String,
        nuevoStock: Int,
        nuevoPrecio: Double,
        nuevoEstado: EstadoProducto
    ) {
        SqliteCloudSql.ejecutar(
            "UPDATE productos SET stock = $nuevoStock, precio = $nuevoPrecio, " +
                "estado = '${nuevoEstado.name}' WHERE id_producto = '${e(idProducto)}';"
        )
    }
}
