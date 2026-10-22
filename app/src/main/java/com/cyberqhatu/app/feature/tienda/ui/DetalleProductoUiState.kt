package com.cyberqhatu.app.feature.tienda.ui

import com.cyberqhatu.app.feature.tienda.domain.EstadoProducto
import com.cyberqhatu.app.feature.tienda.domain.Producto

data class DetalleProductoUiState(
    val producto: Producto = Producto(
        idProducto = "P-001",
        idTienda = "T-001",
        idCategoria = "CAT-001",
        nombre = "Chompa de Alpaca Fina",
        descripcion = "Chompa tejida a mano, 100% fibra de alpaca tradicional, suave al tacto y de alta durabilidad.",
        precio = 180.0,
        stock = 3,
        estado = EstadoProducto.ACTIVO
    ),
    val nombreTienda: String = "Moda Andina",
    val ratingTienda: Double = 4.8,
    val totalResenas: Int = 126,
    val esFavorito: Boolean = false,
    val cargando: Boolean = false
)
