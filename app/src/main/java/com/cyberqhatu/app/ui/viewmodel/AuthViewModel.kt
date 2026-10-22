package com.cyberqhatu.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberqhatu.app.data.local.TokenManager
import com.cyberqhatu.app.data.model.AuthResponse
import com.cyberqhatu.app.data.model.LoginRequest
import com.cyberqhatu.app.data.model.RegisterRequest
import com.cyberqhatu.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val data: AuthResponse) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(request: LoginRequest) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            repository.loginWithCache(request).collect { result ->
                result.onSuccess { response ->
                    tokenManager.saveToken(response.token)
                    _authState.value = AuthState.Success(response)
                }.onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "An unknown error occurred")
                }
            }
        }
    }

    fun register(request: RegisterRequest) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = repository.register(request)
            result.onSuccess { response ->
                tokenManager.saveToken(response.token)
                _authState.value = AuthState.Success(response)
            }.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "An unknown error occurred")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
