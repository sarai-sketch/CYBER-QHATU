package com.cyberqhatu.app.ui.screens.confianza

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReporteFormState(
    val nombreTienda: String = "Moda Andina",
    val motivoSeleccionado: String = "No entregó el producto",
    val descripcion: String = "",
    val evidenciasUris: List<String> = emptyList(),
    val enviando: Boolean = false,
    val reportadoExitoso: Boolean = false,
    val mensajeError: String? = null,
)

class ReportarTiendaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ReporteFormState())
    val uiState: StateFlow<ReporteFormState> = _uiState.asStateFlow()

    fun setNombreTienda(nombre: String) {
        _uiState.update { it.copy(nombreTienda = nombre) }
    }

    fun setMotivo(motivo: String) {
        _uiState.update { it.copy(motivoSeleccionado = motivo) }
    }

    fun setDescripcion(text: String) {
        _uiState.update { it.copy(descripcion = text) }
    }

    fun agregarEvidenciaUri(uri: String) {
        if (_uiState.value.evidenciasUris.size < 3) {
            _uiState.update {
                it.copy(evidenciasUris = it.evidenciasUris + uri)
            }
        }
    }

    fun enviarReporte(onExito: () -> Unit) {
        if (_uiState.value.descripcion.isBlank()) return

        _uiState.update { it.copy(enviando = true) }

        viewModelScope.launch {
            delay(1000)
            _uiState.update {
                it.copy(
                    enviando = false,
                    reportadoExitoso = true,
                )
            }
            onExito()
        }
    }
}
