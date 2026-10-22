package com.cyberqhatu.app.feature.confianza.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReportarTiendaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ReportarTiendaUiState())
    val uiState: StateFlow<ReportarTiendaUiState> = _uiState.asStateFlow()

    fun onMotivoSeleccionadoChanged(nuevoMotivo: String) {
        _uiState.value = _uiState.value.copy(motivoSeleccionado = nuevoMotivo)
    }

    fun onDescripcionChanged(nuevaDescripcion: String) {
        _uiState.value = _uiState.value.copy(descripcion = nuevaDescripcion)
    }

    fun enviarReporte() {
        _uiState.value = _uiState.value.copy(esExitoso = true)
    }
}
