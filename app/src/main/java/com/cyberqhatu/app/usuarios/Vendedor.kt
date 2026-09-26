package com.cyberqhatu.app.usuarios

import java.util.Date

enum class TipoVendedor { INFORMAL, FORMAL }

class Vendedor(
    idUsuario: String,
    nombre: String,
    telefono: String,
    correo: String?,
    ci: String,
    val carnet: String,
    val nit: String? = null,
    var tipo: TipoVendedor = TipoVendedor.INFORMAL,
    var linkWhatsapp: String? = null,
    var usaRegistroVentas: Boolean = false,
    var aceptoTerminos: Boolean = false,
    var fechaAceptacionTerminos: Date? = null
) : Usuario(idUsuario, nombre, telefono, correo, ci) {

    override fun registrarse() { TODO() }
    fun publicarProducto() { TODO() }
    fun editarProducto(idProducto: String) { TODO() }
    fun eliminarProducto(idProducto: String) { TODO() }
    fun responderConsulta(idConsulta: String, respuesta: String) { TODO() }
    fun confirmarVenta(idPedido: String) { TODO() }
    fun confirmarPagoRecibido(idPago: String) { TODO() }
    fun subirBaucherDespacho(idEnvio: String, url: String) { TODO() }
    fun activarRegistroVentas() { TODO() }
    fun verMetricas() { TODO() }
}
