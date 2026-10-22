package com.cyberqhatu.app.feature.perfil.ui

data class PerfilUiState(
    val nombreUsuario: String = "Juan Pérez",
    val correoUsuario: String = "juan.perez@cyberqhatu.bo",
    val telefonoUsuario: String = "+591 70000000",
    val estadoVerificacion: Boolean = true,
    val tipoRol: String = "Vendedor",
    val cargando: Boolean = false
)
