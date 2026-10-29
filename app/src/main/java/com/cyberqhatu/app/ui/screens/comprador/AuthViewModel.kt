package com.cyberqhatu.app.ui.screens.comprador

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cyberqhatu.app.auth.domain.repository.AuthRepository
import com.cyberqhatu.app.auth.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val email: String, val message: String, val isLogin: Boolean) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun login(email: String, contrasena: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authRepository.login(email, contrasena)
            result.fold(
                onSuccess = { isVerified ->
                    if (isVerified) {
                        _authState.value = AuthState.Success(email, "Inicio de sesión exitoso", true)
                    } else {
                        _authState.value = AuthState.Error("Por favor verifica tu correo electrónico. Se ha reenviado un enlace de verificación.")
                    }
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Error al iniciar sesión")
                }
            )
        }
    }

    fun register(nombre: String, email: String, contrasena: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authRepository.register(nombre, email, contrasena)
            result.fold(
                onSuccess = {
                    _authState.value = AuthState.Success(email, "Registro exitoso. Se ha enviado un correo de verificación.", false)
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Error al registrar")
                }
            )
        }
    }

    companion object {
        fun provideFactory(
            repository: AuthRepository = AuthRepositoryImpl()
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(repository) as T
            }
        }
    }
}
