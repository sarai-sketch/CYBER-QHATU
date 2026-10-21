package com.cyberqhatu.app.verificacion.ui

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberqhatu.app.data.local.database.QhatuDatabase
import com.cyberqhatu.app.verificacion.data.repository.VerificacionRepositoryImpl
import com.cyberqhatu.app.verificacion.domain.VerificacionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel siguiendo el patrón MVVM para administrar la captura del Anverso, Reverso y Selfie.
 */
class VerificacionViewModel(
    private var repository: VerificacionRepository = VerificacionRepositoryImpl(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerificacionUiState())
    val uiState: StateFlow<VerificacionUiState> = _uiState.asStateFlow()

    fun inicializarRoom(context: Context, idUsuario: String = "USR-101") {
        val database = QhatuDatabase.getInstance(context)
        repository = VerificacionRepositoryImpl(
            verificacionCacheDao = database.verificacionCacheDao(),
            verificacionDao = database.verificacionDao(),
        )

        viewModelScope.launch(Dispatchers.IO) {
            repository.obtenerEstadoVerificacionLocal(idUsuario).collectLatest { resultadoLocal ->
                if (resultadoLocal != null) {
                    _uiState.update {
                        it.copy(
                            resultado = resultadoLocal,
                            pasoActual = if (resultadoLocal.esExitoso) PasoVerificacion.RESULTADO else it.pasoActual,
                        )
                    }
                }
            }
        }
    }

    fun setFotoCarnetAnverso(bitmap: Bitmap) {
        _uiState.update {
            it.copy(
                fotoCarnetAnverso = bitmap,
                pasoActual = PasoVerificacion.CAPTURAR_CARNET_REVERSO,
            )
        }
    }

    fun setFotoCarnetReverso(bitmap: Bitmap) {
        _uiState.update {
            it.copy(
                fotoCarnetReverso = bitmap,
                pasoActual = PasoVerificacion.CAPTURAR_SELFIE,
            )
        }
    }

    fun setFotoSelfie(bitmap: Bitmap) {
        _uiState.update {
            it.copy(
                fotoSelfie = bitmap,
                pasoActual = PasoVerificacion.PROCESANDO,
                estaCargando = true,
            )
        }
        procesarVerificacion()
    }

    private fun procesarVerificacion() {
        val anverso = _uiState.value.fotoCarnetAnverso ?: return
        val reverso = _uiState.value.fotoCarnetReverso ?: return
        val selfie = _uiState.value.fotoSelfie ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val resultado = repository.verificarIdentidad(anverso, reverso, selfie)
            _uiState.update {
                it.copy(
                    pasoActual = PasoVerificacion.RESULTADO,
                    resultado = resultado,
                    estaCargando = false,
                )
            }
        }
    }

    fun reintentar() {
        _uiState.update {
            VerificacionUiState(pasoActual = PasoVerificacion.CAPTURAR_CARNET_ANVERSO)
        }
    }
}
