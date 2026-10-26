package com.cyberqhatu.app.vendedor.ui

data class ProductoInventario(
    val id: String,
    val titulo: String,
    val categoria: String,
    val precio: Double,
    val stock: Int,
    val ventasRealizadas: Int = 0,
    val estado: EstadoInventario = EstadoInventario.ACTIVO
)
