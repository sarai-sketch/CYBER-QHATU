package com.cyberqhatu.app.feature.inventario.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InventarioVentasViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ResumenVentasState())
    val uiState: StateFlow<ResumenVentasState> = _uiState.asStateFlow()

    fun onTextoBusquedaChanged(nuevoTexto: String) {
        _uiState.value = _uiState.value.copy(textoBusqueda = nuevoTexto)
    }

    fun onFiltroSeleccionadoChanged(filtro: EstadoInventario) {
        _uiState.value = _uiState.value.copy(filtroSeleccionado = filtro)
    }

    fun guardarCambiosProducto(productoId: String, nuevoStock: Int, nuevoPrecio: Double, nuevoEstado: EstadoInventario) {
        val listaActualizada = _uiState.value.listaProductos.map { p ->
            if (p.id == productoId) {
                val estadoFinal = if (nuevoStock <= 0) EstadoInventario.AGOTADO else nuevoEstado
                p.copy(stock = nuevoStock, precio = nuevoPrecio, estado = estadoFinal)
            } else p
        }

        _uiState.value = _uiState.value.copy(
            listaProductos = listaActualizada,
            productosActivos = listaActualizada.count { it.estado == EstadoInventario.ACTIVO },
            productosAgotados = listaActualizada.count { it.estado == EstadoInventario.AGOTADO }
        )
    }

    fun confirmarVentaRapida(productoId: String, cantidad: Int) {
        val prod = _uiState.value.listaProductos.find { it.id == productoId } ?: return
        if (cantidad > 0 && cantidad <= prod.stock) {
            val nuevoStock = prod.stock - cantidad
            val ingresoVenta = prod.precio * cantidad

            val listaActualizada = _uiState.value.listaProductos.map { p ->
                if (p.id == productoId) {
                    p.copy(
                        stock = nuevoStock,
                        ventasRealizadas = p.ventasRealizadas + cantidad,
                        estado = if (nuevoStock == 0) EstadoInventario.AGOTADO else p.estado
                    )
                } else p
            }

            _uiState.value = _uiState.value.copy(
                listaProductos = listaActualizada,
                totalIngresos = _uiState.value.totalIngresos + ingresoVenta,
                totalVentasMes = _uiState.value.totalVentasMes + 1,
                productosActivos = listaActualizada.count { it.estado == EstadoInventario.ACTIVO },
                productosAgotados = listaActualizada.count { it.estado == EstadoInventario.AGOTADO }
            )
        }
    }
}
