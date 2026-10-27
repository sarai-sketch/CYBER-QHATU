package com.cyberqhatu.app.vendedor.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberqhatu.app.data.local.database.QhatuDatabase
import com.cyberqhatu.app.data.local.entity.ProductoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InventarioVentasViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ResumenVentasState())
    val uiState: StateFlow<ResumenVentasState> = _uiState.asStateFlow()

    fun cargarProductosRoom(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val db = QhatuDatabase.getInstance(context)
            db.productoDao().obtenerTodosLosProductos().collectLatest { entidades ->
                val listaProds = entidades.map { e ->
                    ProductoInventario(
                        id = e.idProducto,
                        titulo = e.nombre,
                        categoria = "General",
                        precio = e.precio,
                        stock = e.stock,
                        estado = if (e.stock > 0) EstadoInventario.ACTIVO else EstadoInventario.AGOTADO,
                    )
                }
                _uiState.update {
                    it.copy(
                        listaProductos = if (listaProds.isNotEmpty()) listaProds else it.listaProductos,
                        productosActivos = listaProds.count { p -> p.stock > 0 },
                    )
                }
            }
        }
    }

    fun onBusquedaChange(texto: String) {
        _uiState.update { it.copy(textoBusqueda = texto) }
    }

    fun onFiltroChange(filtro: EstadoInventario) {
        _uiState.update { it.copy(filtroSeleccionado = filtro) }
    }

    fun guardarEdicionProducto(context: Context, idProducto: String, nuevoStock: Int, nuevoPrecio: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val db = QhatuDatabase.getInstance(context)
            val prod = ProductoEntity(
                idProducto = idProducto,
                idTienda = "T-101",
                nombre = "Producto $idProducto",
                descripcion = "Actualizado",
                precio = nuevoPrecio,
                stock = nuevoStock,
                estado = if (nuevoStock > 0) "ACTIVO" else "AGOTADO",
            )
            db.productoDao().insertarProducto(prod)
        }
    }
}
