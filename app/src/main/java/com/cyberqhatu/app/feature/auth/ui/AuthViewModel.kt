package com.cyberqhatu.app.feature.auth.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onTelefonoChanged(input: String) {
        if (input.all { it.isDigit() } && input.length <= 8) {
            _uiState.value = _uiState.value.copy(
                telefono = input,
                esTelefonoValido = input.length >= 8
            )
        }
    }
}
