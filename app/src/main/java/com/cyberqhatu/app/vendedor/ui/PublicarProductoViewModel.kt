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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PublicarProductoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PublicarProductoFormState())
    val uiState: StateFlow<PublicarProductoFormState> = _uiState.asStateFlow()

    fun onTituloChange(nuevo: String) {
        _uiState.update { it.copy(titulo = nuevo, tituloError = null) }
    }

    fun onCategoriaChange(nueva: String) {
        _uiState.update { it.copy(categoria = nueva) }
    }

    fun onPrecioChange(nuevo: String) {
        _uiState.update { it.copy(precio = nuevo, precioError = null) }
    }

    fun onStockChange(nuevo: String) {
        _uiState.update { it.copy(stock = nuevo, stockError = null) }
    }

    fun onDescripcionChange(nueva: String) {
        _uiState.update { it.copy(descripcion = nueva) }
    }

    fun onCondicionChange(nueva: CondicionProducto) {
        _uiState.update { it.copy(condicion = nueva) }
    }

    fun agregarImagenUri(uri: String) {
        if (_uiState.value.imagenesUris.size < 5) {
            _uiState.update {
                it.copy(imagenesUris = it.imagenesUris + uri)
            }
        }
    }

    fun eliminarImagenAt(index: Int) {
        val lista = _uiState.value.imagenesUris.toMutableList()
        if (index in lista.indices) {
            lista.removeAt(index)
            _uiState.update { it.copy(imagenesUris = lista) }
        }
    }

    fun publicarProducto(context: Context, onExito: () -> Unit) {
        val state = _uiState.value
        val precioDouble = state.precio.toDoubleOrNull()
        val stockInt = state.stock.toIntOrNull()

        if (state.titulo.isBlank()) {
            _uiState.update { it.copy(tituloError = "Ingresa un título válido") }
            return
        }
        if (precioDouble == null || precioDouble <= 0) {
            _uiState.update { it.copy(precioError = "Ingresa un precio mayor a 0") }
            return
        }
        if (stockInt == null || stockInt < 1) {
            _uiState.update { it.copy(stockError = "Ingresa un stock válido de al menos 1") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch(Dispatchers.IO) {
            val idNuevo = "PROD-${System.currentTimeMillis()}"
            val producto = com.cyberqhatu.app.tienda.Producto(
                idProducto = idNuevo,
                idTienda = "", 
                nombre = state.titulo,
                descripcion = state.descripcion,
                precio = precioDouble,
                stock = stockInt,
                idCategoria = "c1",
                estado = com.cyberqhatu.app.tienda.EstadoProducto.ACTIVO
            )
            
            val remoteRepo = com.cyberqhatu.app.feature.tienda.data.repository.SqliteCloudProductoRepository()
            remoteRepo.guardarProductoRemoto(producto)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    esExitoso = true,
                )
            }
            onExito()
        }
    }
}
