package com.cyberqhatu.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de Room para la caché local Offline-First (Single Source of Truth).
 */
@Entity(tableName = "verificaciones_cache")
data class VerificacionCacheEntity(
    @PrimaryKey val idVerificacion: String,
    val idUsuario: String,
    val numeroCI: String?,
    val porcentajeSimilitud: Float,
    val esExitoso: Boolean,
    val timestampCache: Long = System.currentTimeMillis(),
    val sincronizadoServidor: Boolean = false,
)
