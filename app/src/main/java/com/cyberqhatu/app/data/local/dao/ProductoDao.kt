package com.cyberqhatu.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberqhatu.app.data.local.entity.MovimientoInventarioEntity
import com.cyberqhatu.app.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones de persistencia en productos e inventario.
 */
@Dao
interface ProductoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProducto(producto: ProductoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarListaProductos(productos: List<ProductoEntity>)

    @Query("SELECT * FROM productos_local ORDER BY fechaCreacion DESC")
    fun obtenerTodosLosProductos(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos_local WHERE idProducto = :idProducto LIMIT 1")
    suspend fun obtenerProductoPorId(idProducto: String): ProductoEntity?

    @Query("UPDATE productos_local SET stock = :nuevoStock, estado = :nuevoEstado WHERE idProducto = :idProducto")
    suspend fun actualizarStockYEstado(idProducto: String, nuevoStock: Int, nuevoEstado: String)

    @Query("UPDATE productos_local SET stock = :nuevoStock, precio = :nuevoPrecio, estado = :nuevoEstado WHERE idProducto = :idProducto")
    suspend fun actualizarProducto(idProducto: String, nuevoStock: Int, nuevoPrecio: Double, nuevoEstado: String)

    @Query("DELETE FROM productos_local WHERE idProducto = :idProducto")
    suspend fun eliminarProducto(idProducto: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registrarMovimientoInventario(movimiento: MovimientoInventarioEntity)

    @Query("SELECT * FROM movimientos_inventario ORDER BY fecha DESC")
    fun obtenerMovimientosInventario(): Flow<List<MovimientoInventarioEntity>>
}
