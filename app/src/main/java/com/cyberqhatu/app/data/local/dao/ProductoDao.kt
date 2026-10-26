package com.cyberqhatu.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberqhatu.app.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para acceder a los productos registrados en la base de datos local.
 */
@Dao
interface ProductoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProducto(producto: ProductoEntity)

    @Query("SELECT * FROM productos ORDER BY fechaCreacion DESC")
    fun obtenerTodosLosProductos(): Flow<List<ProductoEntity>>
}
