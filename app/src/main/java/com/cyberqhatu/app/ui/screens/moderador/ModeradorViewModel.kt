package com.cyberqhatu.app.ui.screens.moderador

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ReporteModeracionUiModel(
    val idReporte: String,
    val idReportante: String,
    val nombreTienda: String,
    val motivo: String,
    val descripcion: String,
    val fecha: String,
    val evidenciasUris: List<String> = emptyList(),
    val estado: String = "PENDIENTE",
)

data class ModeradorUiState(
    val reportesPendientes: List<ReporteModeracionUiModel> = listOf(
        ReporteModeracionUiModel(
            idReporte = "REP-001",
            idReportante = "USR-502",
            nombreTienda = "Moda Andina",
            motivo = "No entregó el producto",
            descripcion = "Realicé el pago por QR y el comerciante no respondió en el punto de encuentro.",
            fecha = "Hace 2 horas",
            evidenciasUris = listOf("evidencia1.png", "evidencia2.png"),
            estado = "PENDIENTE",
        ),
        ReporteModeracionUiModel(
            idReporte = "REP-002",
            idReportante = "USR-301",
            nombreTienda = "ElectroJuegos 16",
            motivo = "Producto distinto al anunciado",
            descripcion = "El modelo enviado tenía raspones y faltaban sus cables originales.",
            fecha = "Ayer",
            evidenciasUris = listOf("evidencia3.png"),
            estado = "PENDIENTE",
        ),
    ),
    val reporteSeleccionado: ReporteModeracionUiModel? = null,
    val cargando: Boolean = false,
    val accionRealizadaExitosamente: Boolean = false,
)

class ModeradorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ModeradorUiState())
    val uiState: StateFlow<ModeradorUiState> = _uiState.asStateFlow()

    fun seleccionarReporte(idReporte: String) {
        val rep = _uiState.value.reportesPendientes.firstOrNull { it.idReporte == idReporte }
        _uiState.update { it.copy(reporteSeleccionado = rep) }
    }

    fun sancionarTienda(idReporte: String, motivoSancion: String) {
        _uiState.update { state ->
            val listaActualizada = state.reportesPendientes.map { r ->
                if (r.idReporte == idReporte) r.copy(estado = "RESUELTO_SANCIONADO") else r
            }
            state.copy(
                reportesPendientes = listaActualizada,
                accionRealizadaExitosamente = true,
            )
        }
    }

    fun desestimarReporte(idReporte: String) {
        _uiState.update { state ->
            val listaActualizada = state.reportesPendientes.map { r ->
                if (r.idReporte == idReporte) r.copy(estado = "RESUELTO_DESESTIMADO") else r
            }
            state.copy(
                reportesPendientes = listaActualizada,
                accionRealizadaExitosamente = true,
            )
        }
    }
}
