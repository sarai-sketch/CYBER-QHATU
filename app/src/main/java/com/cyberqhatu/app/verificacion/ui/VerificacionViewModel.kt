package com.cyberqhatu.app.verificacion.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberqhatu.app.verificacion.data.repository.VerificacionRepositoryImpl
import com.cyberqhatu.app.verificacion.domain.VerificacionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel siguiendo el patrón MVVM para administrar el estado de verificación.
 */
class VerificacionViewModel(
    private val repository: VerificacionRepository = VerificacionRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerificacionUiState())
    val uiState: StateFlow<VerificacionUiState> = _uiState.asStateFlow()

    fun setFotoCarnet(bitmap: Bitmap) {
        _uiState.update {
            it.copy(
                fotoCarnet = bitmap,
                pasoActual = PasoVerificacion.CAPTURAR_SELFIE
            )
        }
    }

    fun setFotoSelfie(bitmap: Bitmap) {
        _uiState.update {
            it.copy(
                fotoSelfie = bitmap,
                pasoActual = PasoVerificacion.PROCESANDO,
                estaCargando = true
            )
        }
        procesarVerificacion()
    }

    private fun procesarVerificacion() {
        val carnet = _uiState.value.fotoCarnet ?: return
        val selfie = _uiState.value.fotoSelfie ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val resultado = repository.verificarIdentidad(carnet, selfie)
            _uiState.update {
                it.copy(
                    pasoActual = PasoVerificacion.RESULTADO,
                    resultado = resultado,
                    estaCargando = false
                )
            }
        }
    }

    fun reintentar() {
        _uiState.update {
            VerificacionUiState(pasoActual = PasoVerificacion.CAPTURAR_CARNET)
        }
    }
}
