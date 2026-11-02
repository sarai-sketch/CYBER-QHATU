package com.cyberqhatu.app.ui.screens.tienda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberqhatu.app.feature.tienda.data.repository.SqliteCloudCategoriaRepository
import com.cyberqhatu.app.tienda.Categoria
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class CategoriaUiState(
    val nombreNuevaCategoria: String = "",
    val categoriasGuardadas: List<Categoria> = emptyList(),
    val guardando: Boolean = false,
    val error: String? = null,
    val exito: Boolean = false
)

class CategoriaViewModel : ViewModel() {

    private val repository = SqliteCloudCategoriaRepository()

    private val _uiState = MutableStateFlow(CategoriaUiState())
    val uiState: StateFlow<CategoriaUiState> = _uiState.asStateFlow()

    init {
        cargarCategorias()
    }

    private fun cargarCategorias() {
        viewModelScope.launch {
            repository.obtenerCategoriasRemotas().collect { lista ->
                _uiState.update { it.copy(categoriasGuardadas = lista) }
            }
        }
    }

    fun onNombreChange(nuevoNombre: String) {
        _uiState.update { it.copy(nombreNuevaCategoria = nuevoNombre, error = null, exito = false) }
    }

    fun guardarCategoria() {
        val nombre = _uiState.value.nombreNuevaCategoria.trim()
        if (nombre.isEmpty()) {
            _uiState.update { it.copy(error = "El nombre no puede estar vacío") }
            return
        }

        _uiState.update { it.copy(guardando = true, error = null, exito = false) }

        viewModelScope.launch {
            val nuevaCategoria = Categoria(
                idCategoria = "cat-${UUID.randomUUID().toString().take(8)}",
                nombre = nombre,
                idCategoriaPadre = null // Por defecto es categoría raíz
            )

            val success = repository.guardarCategoriaRemota(nuevaCategoria)

            if (success) {
                _uiState.update { state ->
                    state.copy(
                        guardando = false,
                        exito = true,
                        nombreNuevaCategoria = "", // Limpiar campo
                        categoriasGuardadas = state.categoriasGuardadas + nuevaCategoria
                    )
                }
            } else {
                _uiState.update { it.copy(guardando = false, error = "Error al guardar en la nube") }
            }
        }
    }

    fun resetEstado() {
        _uiState.update { it.copy(error = null, exito = false) }
    }
}
