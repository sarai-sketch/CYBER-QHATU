package com.cyberqhatu.app.ui.screens.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cyberqhatu.app.auth.domain.repository.AuthRepository
import com.cyberqhatu.app.auth.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UpdateState {
    object Idle : UpdateState()
    object Loading : UpdateState()
    data class Success(val newName: String) : UpdateState()
    data class Error(val message: String) : UpdateState()
}

class PerfilViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    fun resetState() {
        _updateState.value = UpdateState.Idle
    }

    fun actualizarNombre(nuevoNombre: String) {
        _updateState.value = UpdateState.Loading
        viewModelScope.launch {
            val result = authRepository.updateProfileName(nuevoNombre)
            result.fold(
                onSuccess = {
                    _updateState.value = UpdateState.Success(nuevoNombre.trim())
                },
                onFailure = { exception ->
                    _updateState.value = UpdateState.Error(exception.message ?: "Error al actualizar")
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
                return PerfilViewModel(repository) as T
            }
        }
    }
}
