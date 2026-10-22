package com.cyberqhatu.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberqhatu.app.data.model.AuthResponse
import com.cyberqhatu.app.data.model.LoginRequest
import com.cyberqhatu.app.data.model.RegisterRequest
import com.cyberqhatu.app.data.model.ResendCodeRequest
import com.cyberqhatu.app.data.model.VerifyEmailRequest
import com.cyberqhatu.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val data: AuthResponse) : AuthState()
    data class RegisterSuccess(val message: String, val email: String) : AuthState()
    data class VerifySuccess(val message: String) : AuthState()
    data class ResendSuccess(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(request: LoginRequest) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            repository.login(request).collect { result ->
                result.onSuccess { response ->
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
            result.onSuccess { message ->
                _authState.value = AuthState.RegisterSuccess(message, request.email)
            }.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "An unknown error occurred")
            }
        }
    }

    fun verifyEmail(request: VerifyEmailRequest) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = repository.verifyEmail(request)
            result.onSuccess { message ->
                _authState.value = AuthState.VerifySuccess(message)
            }.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "An unknown error occurred")
            }
        }
    }

    fun resendCode(request: ResendCodeRequest) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = repository.resendCode(request)
            result.onSuccess { message ->
                _authState.value = AuthState.ResendSuccess(message)
            }.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "An unknown error occurred")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
