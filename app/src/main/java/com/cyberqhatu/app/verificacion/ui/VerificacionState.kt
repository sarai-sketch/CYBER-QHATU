package com.cyberqhatu.app.verificacion.ui

import android.graphics.Bitmap
import com.cyberqhatu.app.verificacion.domain.ResultadoVerificacion

data class VerificacionUiState(
    val pasoActual: PasoVerificacion = PasoVerificacion.INSTRUCCIONES,
    val fotoCarnetAnverso: Bitmap? = null,
    val fotoCarnetReverso: Bitmap? = null,
    val fotoSelfie: Bitmap? = null,
    val estaCargando: Boolean = false,
    val resultado: ResultadoVerificacion? = null,
    val mensajeInfoStep: String = "Saca una foto nítida al anverso de tu Carnet de Identidad"
)
