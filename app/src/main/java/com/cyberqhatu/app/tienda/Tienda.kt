package com.cyberqhatu.app.tienda

enum class TipoTienda { FISICA, VIRTUAL }
enum class EstadoTienda { ACTIVA, SUSPENDIDA }

class Tienda(
    val idTienda: String,
    var nombre: String,
    var tipo: TipoTienda,
    var direccionGoogleMaps: String? = null,
    var horarioApertura: String? = null,
    var horarioCierre: String? = null,
    var qrPago: String? = null,
    var linkWhatsapp: String? = null,
    var estado: EstadoTienda = EstadoTienda.ACTIVA
) {
    fun validarTienda() { TODO() }
    fun actualizarInfo() { TODO() }
    fun agregarFoto(url: String) { TODO() }
}
