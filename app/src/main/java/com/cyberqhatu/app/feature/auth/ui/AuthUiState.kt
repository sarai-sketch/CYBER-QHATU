package com.cyberqhatu.app.feature.auth.ui

data class AuthUiState(
    val telefono: String = "",
    val esTelefonoValido: Boolean = false,
    val cargando: Boolean = false,
    val mensajeError: String? = null
)
