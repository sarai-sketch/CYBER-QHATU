package com.cyberqhatu.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberqhatu.app.data.local.entity.VerificacionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para realizar operaciones CRUD en la tabla de verificaciones.
 */
@Dao
interface VerificacionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVerificacion(verificacion: VerificacionEntity)

    @Query("SELECT * FROM verificaciones_identidad WHERE idUsuario = :idUsuario ORDER BY fechaVerificacion DESC LIMIT 1")
    fun obtenerUltimaVerificacionPorUsuario(idUsuario: String): Flow<VerificacionEntity?>

    @Query("DELETE FROM verificaciones_identidad WHERE idUsuario = :idUsuario")
    suspend fun eliminarVerificacionPorUsuario(idUsuario: String)
}
