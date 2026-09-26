package com.cyberqhatu.app.confianza

enum class EstadoReporte { PENDIENTE, EN_REVISION, RESUELTO }

class Reporte(
    val idReporte: String,
    val idReportante: String,
    val idReportado: String,
    val idPedido: String? = null,
    var motivo: String,
    var descripcion: String,
    var estado: EstadoReporte = EstadoReporte.PENDIENTE
) {
    fun enviar() { TODO() }
    fun refutar(descargo: String) { TODO() }
    fun revisar() { TODO() }
    fun resolver() { TODO() }
}
