package com.cyberqhatu.app.ui.screens.vendedor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberqhatu.app.feature.tienda.data.repository.SqliteCloudTiendaRepository
import com.cyberqhatu.app.tienda.Tienda
import com.cyberqhatu.app.tienda.TipoTienda
import kotlinx.coroutines.launch
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
    val idTienda: String = "",
    val nombreTienda: String = "",
    val descripcion: String = "",
    val fotoLogoUri: String? = null,
    val fotoPortadaUri: String? = null,
    val horarioApertura: String = "",
    val horarioCierre: String = "",
    val linkWhatsapp: String = "",
    val colaboradores: List<ColaboradorUiModel> = emptyList(),
    val guardando: Boolean = false,
    val guardadoExitoso: Boolean = false,
)

class GestionTiendaViewModel : ViewModel() {

    private val repository = SqliteCloudTiendaRepository()
    private var tiendaOriginal: Tienda? = null

    private val _uiState = MutableStateFlow(GestionTiendaUiState())
    val uiState: StateFlow<GestionTiendaUiState> = _uiState.asStateFlow()

    fun cargarTienda(idTienda: String) {
        viewModelScope.launch {
            // Buscamos la tienda entre las remotas (por simplicidad, filtramos la lista)
            repository.obtenerTiendasRemotas().collect { lista ->
                val tienda = lista.firstOrNull { it.idTienda == idTienda }
                if (tienda != null) {
                    tiendaOriginal = tienda
                    _uiState.update { it.copy(
                        idTienda = tienda.idTienda,
                        nombreTienda = tienda.nombre,
                        descripcion = tienda.descripcion,
                        horarioApertura = tienda.horarioApertura ?: "08:00",
                        horarioCierre = tienda.horarioCierre ?: "18:00",
                        linkWhatsapp = tienda.linkWhatsapp ?: ""
                    ) }
                }
            }
        }
    }

    fun guardarCambiosBaseDeDatos() {
        val actual = tiendaOriginal ?: return
        
        _uiState.update { it.copy(guardando = true) }
        
        viewModelScope.launch {
            val tiendaActualizada = actual.apply {
                descripcion = _uiState.value.descripcion
                horarioApertura = _uiState.value.horarioApertura
                horarioCierre = _uiState.value.horarioCierre
            }
            
            val exito = repository.guardarTiendaRemota(tiendaActualizada)
            _uiState.update { it.copy(guardando = false, guardadoExitoso = exito) }
        }
    }

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
