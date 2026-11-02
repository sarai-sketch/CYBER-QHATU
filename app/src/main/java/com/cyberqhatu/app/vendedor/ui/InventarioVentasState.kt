package com.cyberqhatu.app.vendedor.ui

data class ResumenVentasState(
    val totalIngresos: Double = 0.0,
    val totalVentasMes: Int = 0,
    val productosActivos: Int = 0,
    val productosAgotados: Int = 0,
    val textoBusqueda: String = "",
    val filtroSeleccionado: EstadoInventario = EstadoInventario.TODOS,
    val listaProductos: List<ProductoInventario> = emptyList(),
    val isLoading: Boolean = false
)
