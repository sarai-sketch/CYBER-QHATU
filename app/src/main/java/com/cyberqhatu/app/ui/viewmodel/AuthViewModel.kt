package com.cyberqhatu.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberqhatu.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class EmailSent(val message: String = "Correo de verificación enviado") : AuthState()
    object EmailNotVerified : AuthState()
    object Verified : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun checkCurrentSession() {
        val user = repository.currentUser
        if (user != null) {
            if (user.isEmailVerified) {
                _authState.value = AuthState.Verified
            } else {
                _authState.value = AuthState.EmailNotVerified
            }
        } else {
            _authState.value = AuthState.Idle
        }
    }

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Por favor completa todos los campos")
            return
        }
        if (pass.length < 8) {
            _authState.value = AuthState.Error("La contraseña debe tener al menos 8 caracteres")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            repository.login(email, pass)
                .onSuccess { isVerified ->
                    if (isVerified) {
                        _authState.value = AuthState.Verified
                    } else {
                        _authState.value = AuthState.EmailNotVerified
                    }
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Error al iniciar sesión")
                }
        }
    }

    fun register(name: String, email: String, pass: String) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Por favor completa todos los campos")
            return
        }
        if (pass.length < 8) {
            _authState.value = AuthState.Error("La contraseña debe tener al menos 8 caracteres")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            repository.register(name, email, pass)
                .onSuccess {
                    _authState.value = AuthState.EmailSent("Se ha enviado un enlace de verificación a tu correo")
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Error al registrarse")
                }
        }
    }

    fun resendVerificationEmail() {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            repository.sendVerificationEmail()
                .onSuccess {
                    _authState.value = AuthState.EmailSent("Reenvío exitoso. Revisa tu bandeja de entrada")
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Error al reenviar correo")
                }
        }
    }

    fun reloadAndCheckVerified() {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            repository.reloadAndCheckVerified()
                .onSuccess { isVerified ->
                    if (isVerified) {
                        _authState.value = AuthState.Verified
                    } else {
                        _authState.value = AuthState.Error("Aún no verificaste tu correo")
                    }
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Error al verificar estado")
                }
        }
    }

    fun logout() {
        repository.logout()
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
