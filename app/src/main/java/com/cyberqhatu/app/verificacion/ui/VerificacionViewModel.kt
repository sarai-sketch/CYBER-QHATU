package com.cyberqhatu.app.verificacion.ui

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberqhatu.app.data.local.TokenManager
import com.cyberqhatu.app.data.api.RetrofitClient
import com.cyberqhatu.app.data.local.database.QhatuDatabase
import com.cyberqhatu.app.verificacion.data.repository.VerificacionRepositoryImpl
import com.cyberqhatu.app.verificacion.domain.VerificacionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel siguiendo el patrón MVVM para administrar la captura del Anverso, Reverso y Selfie.
 */
class VerificacionViewModel(
    private var repository: VerificacionRepository? = null,
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerificacionUiState())
    val uiState: StateFlow<VerificacionUiState> = _uiState.asStateFlow()
    private var currentUserId: String? = null

    fun inicializarRoom(context: Context) {
        val database = QhatuDatabase.getInstance(context)
        val tokenManager = TokenManager(context)
        
        repository = VerificacionRepositoryImpl(
            verificacionCacheDao = database.verificacionCacheDao(),
            verificacionDao = database.verificacionDao(),
            apiService = RetrofitClient.verificacionApiService
        )

        viewModelScope.launch(Dispatchers.IO) {
            val token = tokenManager.getToken().firstOrNull()
            // We use token as idUsuario temporarily since AuthRepository uses uid for token.
            val idUsuario = token ?: "USR-101"
            currentUserId = idUsuario
            
            repository?.obtenerEstadoVerificacionLocal(idUsuario)?.collectLatest { resultadoLocal ->
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
        val repo = repository ?: return
        val idUser = currentUserId ?: "USR-101"

        viewModelScope.launch(Dispatchers.IO) {
            val resultado = repo.verificarIdentidad(idUser, anverso, reverso, selfie)
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
