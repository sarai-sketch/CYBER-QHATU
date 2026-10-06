package com.cyberqhatu.app.usuarios

import java.util.Date

enum class TipoVendedor { INFORMAL, FORMAL }

class Vendedor(
    idUsuario: String,
    nombre: String,
    correo: String,
    telefonoWhatsapp: String,
    fotoPerfil: String?,
    estadoVerificacionCI: Boolean,
    ci: String,
    fotoCI: String?,
    fotoBiometricaFacial: String?,
    fechaRegistro: Date = Date(),

    // Atributos específicos del Vendedor
    var estadoVerificacion: Boolean = false,
    val carnet: String,
    val nit: String? = null,
    var tipo: TipoVendedor = TipoVendedor.INFORMAL,
    var linkWhatsappPersonal: String? = null,
    var usaRegistroVentas: Boolean = false,
    var aceptoTerminos: Boolean = false,
    var fechaAceptacionTerminos: Date? = null
) : Usuario(
    idUsuario = idUsuario,
    nombre = nombre,
    correo = correo,
    telefonoWhatsapp = telefonoWhatsapp,
    fotoPerfil = fotoPerfil,
    estadoVerificacionCI = estadoVerificacionCI,
    ci = ci,
    fotoCI = fotoCI,
    fotoBiometricaFacial = fotoBiometricaFacial,
    fechaRegistro = fechaRegistro
) {

    override fun registrarse(): Boolean {
        // Lógica de registro para el vendedor
        return true
    }

    fun crearTienda(nombreTienda: String, tipoTiendaNombre: String): Any {
        // Retorna una instancia genérica o la clase Tienda de tu paquete tienda
        return "Tienda creada: $nombreTienda"
    }

    fun aceptarInvitacion(idInvitacion: String) {
        // Lógica para aceptar ser miembro de una tienda
    }

    // Métodos adicionales de gestión del vendedor
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