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
    val listaProductos: List<ProductoInventario> = listOf(
        ProductoInventario("1", "Laptop Gamer ASUS ROG 16GB", "Electrónica", 4299.00, 5, 12, EstadoInventario.ACTIVO),
        ProductoInventario("2", "Audífonos Bluetooth Noise Cancelling", "Tecnología", 299.90, 18, 24, EstadoInventario.ACTIVO),
        ProductoInventario("3", "Silla Ergonómica de Oficina", "Hogar", 450.00, 0, 8, EstadoInventario.AGOTADO),
        ProductoInventario("4", "Mochila Impermeable para Laptop", "Accesorios", 120.00, 15, 15, EstadoInventario.ACTIVO),
        ProductoInventario("5", "Teclado Mecánico RGB Red Switch", "Tecnología", 189.00, 3, 5, EstadoInventario.PAUSADO)
    ),
    val isLoading: Boolean = false
)
