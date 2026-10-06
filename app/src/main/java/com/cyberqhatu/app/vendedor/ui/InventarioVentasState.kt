package com.cyberqhatu.app.vendedor.ui

/**
 * Estado local independiente para la Pantalla 8: Inventario y Ventas (D2).
 * Modelos de datos autocontenidos para el panel de gestión del vendedor.
 */
data class ProductoInventario(
    val id: String,
    val titulo: String,
    val categoria: String,
    val precio: Double,
    val stock: Int,
    val ventasRealizadas: Int = 0,
    val estado: EstadoInventario = EstadoInventario.ACTIVO
)

enum class EstadoInventario(val tituloMostrar: String) {
    TODOS("Todos"),
    ACTIVO("Activo"),
    AGOTADO("Agotado"),
    PAUSADO("Pausado")
}

data class ResumenVentasState(
    val totalIngresos: Double = 3450.50,
    val totalVentasMes: Int = 28,
    val productosActivos: Int = 12,
    val productosAgotados: Int = 2,
    val textoBusqueda: String = "",
    val filtroSeleccionado: EstadoInventario = EstadoInventario.TODOS,
    val listaProductos: List<ProductoInventario> = emptyList(),
    val isLoading: Boolean = false
)
