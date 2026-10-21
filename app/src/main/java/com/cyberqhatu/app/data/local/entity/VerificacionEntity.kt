package com.cyberqhatu.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de Room para la tabla de verificaciones de identidad en la base de datos local.
 */
@Entity(tableName = "verificaciones_identidad")
data class VerificacionEntity(
    @PrimaryKey val idVerificacion: String,
    val idUsuario: String,
    val numeroCI: String?,
    val porcentajeSimilitud: Float,
    val esExitoso: Boolean,
    val fechaVerificacion: Long = System.currentTimeMillis(),
)
