package com.cyberqhatu.app.feature.tienda.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DetalleProductoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleProductoUiState())
    val uiState: StateFlow<DetalleProductoUiState> = _uiState.asStateFlow()

    fun toggleFavorito() {
        _uiState.value = _uiState.value.copy(
            esFavorito = !_uiState.value.esFavorito
        )
    }
}
