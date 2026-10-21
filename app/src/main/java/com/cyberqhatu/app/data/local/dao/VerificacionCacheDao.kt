package com.cyberqhatu.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberqhatu.app.data.local.entity.VerificacionCacheEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO de Caché para soportar la arquitectura Offline-First.
 */
@Dao
interface VerificacionCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCache(cacheEntity: VerificacionCacheEntity)

    @Query("SELECT * FROM verificaciones_cache WHERE idUsuario = :idUsuario ORDER BY timestampCache DESC LIMIT 1")
    fun obtenerCachePorUsuario(idUsuario: String): Flow<VerificacionCacheEntity?>

    @Query("UPDATE verificaciones_cache SET sincronizadoServidor = 1 WHERE idVerificacion = :idVerificacion")
    suspend fun marcarComoSincronizado(idVerificacion: String)

    @Query("DELETE FROM verificaciones_cache WHERE idUsuario = :idUsuario")
    suspend fun limpiarCacheUsuario(idUsuario: String)
}
