package com.cyberqhatu.app.ui.screens.vendedor

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ColaboradorUiModel(
    val idMiembro: String,
    val nombre: String,
    val correo: String,
    val rol: String,
)

data class GestionTiendaUiState(
    val idTienda: String = "T-101",
    val nombreTienda: String = "Moda Andina",
    val descripcion: String = "Tienda especializada en prendas tradicionales y artesanía fina en la Feria 16 de Julio.",
    val fotoLogoUri: String? = null,
    val fotoPortadaUri: String? = null,
    val horarioApertura: String = "08:00",
    val horarioCierre: String = "18:00",
    val linkWhatsapp: String = "+59170000000",
    val colaboradores: List<ColaboradorUiModel> = listOf(
        ColaboradorUiModel("1", "María Flores", "maria@cyberqhatu.bo", "ADMINISTRADOR"),
        ColaboradorUiModel("2", "Carlos Quispe", "carlos@cyberqhatu.bo", "VENDEDOR"),
    ),
    val guardando: Boolean = false,
    val guardadoExitoso: Boolean = false,
)

class GestionTiendaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GestionTiendaUiState())
    val uiState: StateFlow<GestionTiendaUiState> = _uiState.asStateFlow()

    fun onDescripcionChange(nueva: String) {
        _uiState.update { it.copy(descripcion = nueva) }
    }

    fun onHorariosChange(apertura: String, cierre: String) {
        _uiState.update { it.copy(horarioApertura = apertura, horarioCierre = cierre) }
    }

    fun onFotoLogoUriChange(uri: String) {
        _uiState.update { it.copy(fotoLogoUri = uri) }
    }

    fun onFotoPortadaUriChange(uri: String) {
        _uiState.update { it.copy(fotoPortadaUri = uri) }
    }

    fun agregarColaborador(nombre: String, correo: String, rol: String) {
        val nuevo = ColaboradorUiModel(
            idMiembro = "M-${System.currentTimeMillis()}",
            nombre = nombre,
            correo = correo,
            rol = rol,
        )
        _uiState.update {
            it.copy(colaboradores = it.colaboradores + nuevo)
        }
    }

    fun eliminarColaborador(idMiembro: String) {
        _uiState.update {
            it.copy(colaboradores = it.colaboradores.filter { c -> c.idMiembro != idMiembro })
        }
    }
}
