package com.cyberqhatu.app.feature.publicacion.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PublicarProductoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PublicarProductoFormState())
    val uiState: StateFlow<PublicarProductoFormState> = _uiState.asStateFlow()

    fun onTituloChanged(nuevoTitulo: String) {
        val error = if (nuevoTitulo.isBlank()) "El título no puede estar vacío" else null
        _uiState.value = _uiState.value.copy(titulo = nuevoTitulo, tituloError = error)
    }

    fun onCategoriaChanged(nuevaCategoria: String) {
        _uiState.value = _uiState.value.copy(categoria = nuevaCategoria)
    }

    fun onPrecioChanged(nuevoPrecio: String) {
        val error = when {
            nuevoPrecio.isBlank() -> "Requerido"
            nuevoPrecio.toDoubleOrNull() == null -> "Inválido"
            else -> null
        }
        _uiState.value = _uiState.value.copy(precio = nuevoPrecio, precioError = error)
    }

    fun onStockChanged(nuevoStock: String) {
        val error = when {
            nuevoStock.isBlank() -> "Requerido"
            nuevoStock.toIntOrNull() == null || nuevoStock.toInt() < 1 -> ">= 1"
            else -> null
        }
        _uiState.value = _uiState.value.copy(stock = nuevoStock, stockError = error)
    }

    fun onDescripcionChanged(nuevaDescripcion: String) {
        _uiState.value = _uiState.value.copy(descripcion = nuevaDescripcion)
    }

    fun onCondicionChanged(nuevaCondicion: CondicionProducto) {
        _uiState.value = _uiState.value.copy(condicion = nuevaCondicion)
    }

    fun agregarFoto() {
        if (_uiState.value.imagenesUris.size < 5) {
            val nuevaImagen = "Foto #${_uiState.value.imagenesUris.size + 1}"
            _uiState.value = _uiState.value.copy(
                imagenesUris = _uiState.value.imagenesUris + nuevaImagen
            )
        }
    }

    fun eliminarFoto(index: Int) {
        val listaActualizada = _uiState.value.imagenesUris.toMutableList()
        if (index in listaActualizada.indices) {
            listaActualizada.removeAt(index)
            _uiState.value = _uiState.value.copy(imagenesUris = listaActualizada)
        }
    }

    fun publicarProducto() {
        val formState = _uiState.value
        val errorTitulo = if (formState.titulo.isBlank()) "Ingresa un título válido" else null
        val errorPrecio = when {
            formState.precio.isBlank() -> "Ingresa un precio"
            formState.precio.toDoubleOrNull() == null || formState.precio.toDouble() <= 0 -> "Precio mayor a 0"
            else -> null
        }
        val errorStock = when {
            formState.stock.isBlank() -> "Ingresa el stock"
            formState.stock.toIntOrNull() == null || formState.stock.toInt() < 1 -> "Stock mínimo 1"
            else -> null
        }

        if (errorTitulo != null || errorPrecio != null || errorStock != null) {
            _uiState.value = formState.copy(
                tituloError = errorTitulo,
                precioError = errorPrecio,
                stockError = errorStock,
                mensajeError = "Por favor corrige los campos requeridos en el formulario."
            )
        } else {
            _uiState.value = formState.copy(
                mensajeError = null,
                esExitoso = true
            )
        }
    }

    fun resetForm() {
        _uiState.value = PublicarProductoFormState()
    }
}
