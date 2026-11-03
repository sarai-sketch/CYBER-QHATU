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
 * Productos, inventario y ventas con SQL directo contra SQLite Cloud.
 * Las funciones "suspend" sin sufijo lanzan ApiException si algo falla (para mostrar el error);
 * las versiones antiguas (Flow / Boolean) se mantienen para no romper el resto de pantallas.
 */
class SqliteCloudProductoRepository {

    private fun e(s: String?) = SqliteCloudSql.esc(s)

    private fun filaAProducto(row: Map<String, Any?>): Producto? {
        val idProd = row["id_producto"]?.toString() ?: return null
        val estadoStr = row["estado"]?.toString() ?: "ACTIVO"
        return Producto(
            idProducto = idProd,
            idTienda = row["id_tienda"]?.toString() ?: "",
            idCategoria = row["id_categoria"]?.toString() ?: "c1",
            nombre = row["nombre"]?.toString() ?: "Producto",
            descripcion = row["descripcion"]?.toString() ?: "",
            precio = row["precio"]?.toString()?.toDoubleOrNull() ?: 0.0,
            stock = row["stock"]?.toString()?.toDoubleOrNull()?.toInt() ?: 0,
            estado = if (estadoStr == "ACTIVO") EstadoProducto.ACTIVO else EstadoProducto.OCULTO
        )
    }

    // ---------- Lecturas ----------

    /** Todos los productos (catalogo de Home y perfiles). Si falla, registra el error y devuelve lista vacia. */
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

    /** Productos de UNA tienda (inventario). Lanza ApiException si falla. */
    suspend fun obtenerProductosDeTienda(idTienda: String): List<Producto> {
        if (idTienda.isBlank()) throw ApiException(null, "No se conoce la tienda")
        return SqliteCloudSql.ejecutar(
            "SELECT * FROM productos WHERE id_tienda = '${e(idTienda)}' ORDER BY rowid DESC;"
        ).mapNotNull { filaAProducto(it) }
    }

    /** Ingresos y numero de ventas del mes actual de la tienda. */
    suspend fun estadisticasMes(idTienda: String): Pair<Double, Int> {
        val fila = SqliteCloudSql.ejecutar(
            "SELECT COALESCE(SUM(cantidad * precio_unitario), 0) AS ingresos, COUNT(*) AS ventas " +
                "FROM ventas_registro WHERE id_tienda = '${e(idTienda)}' " +
                "AND fecha >= (strftime('%s', 'now', 'start of month') * 1000);"
        ).firstOrNull() ?: return 0.0 to 0
        val ingresos = fila["ingresos"]?.toString()?.toDoubleOrNull() ?: 0.0
        val ventas = fila["ventas"]?.toString()?.toDoubleOrNull()?.toInt() ?: 0
        return ingresos to ventas
    }

    // ---------- Escrituras ----------

    /** Inserta/reemplaza un producto. Lanza ApiException si falla. */
    suspend fun guardarProducto(producto: Producto) {
        if (producto.idTienda.isBlank()) throw ApiException(null, "No se conoce la tienda del producto")
        val id = producto.idProducto.ifBlank { "p-${System.currentTimeMillis()}" }
        val valoresBase = "'${e(id)}', '${e(producto.idTienda)}', '${e(producto.nombre)}', " +
            "'${e(producto.descripcion)}', ${producto.precio}, ${producto.stock}, '${producto.estado.name}', 'NUEVO'"
        try {
            SqliteCloudSql.ejecutar(
                "INSERT OR REPLACE INTO productos (id_producto, id_tienda, nombre, descripcion, precio, stock, estado, condicion, id_categoria) " +
                    "VALUES ($valoresBase, '${e(producto.idCategoria.ifBlank { "c1" })}');"
            )
        } catch (ex: ApiException) {
            // Si la tabla aun no tiene la columna id_categoria, guardar sin ella
            if (ex.message?.contains("id_categoria") == true) {
                SqliteCloudSql.ejecutar(
                    "INSERT OR REPLACE INTO productos (id_producto, id_tienda, nombre, descripcion, precio, stock, estado, condicion) " +
                        "VALUES ($valoresBase);"
                )
            } else {
                throw ex
            }
        }
    }

    suspend fun actualizarProducto(idProducto: String, nuevoStock: Int, nuevoPrecio: Double, nuevoEstado: EstadoProducto) {
        SqliteCloudSql.ejecutar(
            "UPDATE productos SET stock = $nuevoStock, precio = $nuevoPrecio, estado = '${nuevoEstado.name}' " +
                "WHERE id_producto = '${e(idProducto)}';"
        )
    }

    /**
     * Registra la venta (tabla ventas_registro) y descuenta el stock.
     * Primero la venta: si falla, no se toca el stock.
     */
    suspend fun registrarVenta(
        idTienda: String,
        idProducto: String,
        nombreProducto: String,
        cantidad: Int,
        precioUnitario: Double,
        canalVenta: String
    ) {
        if (cantidad <= 0) throw ApiException(null, "Cantidad no valida")
        val idVenta = "v-${System.currentTimeMillis()}-${(1000..9999).random()}"
        SqliteCloudSql.ejecutar(
            "INSERT INTO ventas_registro (id_venta, id_tienda, id_producto, nombre_producto, cantidad, precio_unitario, canal_venta) " +
                "VALUES ('$idVenta', '${e(idTienda)}', '${e(idProducto)}', '${e(nombreProducto)}', $cantidad, $precioUnitario, '${e(canalVenta)}');"
        )
        SqliteCloudSql.ejecutar(
            "UPDATE productos SET " +
                "estado = CASE WHEN stock - $cantidad <= 0 THEN 'OCULTO' ELSE estado END, " +
                "stock = MAX(stock - $cantidad, 0) " +
                "WHERE id_producto = '${e(idProducto)}';"
        )
    }

    // ---------- Compatibilidad con codigo anterior ----------

    suspend fun guardarProductoRemoto(producto: Producto): Boolean {
        return try {
            guardarProducto(producto)
            true
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            Log.e("SqliteCloud", "Error al guardar producto remoto: ${ex.message}", ex)
            false
        }
    }

    suspend fun actualizarStockYPrecioRemoto(idProducto: String, nuevoStock: Int, nuevoPrecio: Double, nuevoEstado: EstadoProducto): Boolean {
        return try {
            actualizarProducto(idProducto, nuevoStock, nuevoPrecio, nuevoEstado)
            true
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            Log.e("SqliteCloud", "Error al actualizar stock/precio remoto: ${ex.message}", ex)
            false
        }
    }
}
