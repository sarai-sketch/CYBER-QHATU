package com.cyberqhatu.app.feature.confianza.ui

data class ReportarTiendaUiState(
    val nombreTienda: String = "Moda Andina",
    val motivoSeleccionado: String = "No entregó el producto",
    val motivosDisponibles: List<String> = listOf(
        "No entregó el producto",
        "Producto distinto al anunciado",
        "Trato irrespetuoso"
    ),
    val descripcion: String = "",
    val cargando: Boolean = false,
    val esExitoso: Boolean = false
)
