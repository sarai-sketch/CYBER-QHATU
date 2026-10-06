package com.cyberqhatu.app.usuarios

import java.util.Date

class Comprador(
    idUsuario: String,
    nombre: String,
    correo: String,
    telefonoWhatsapp: String,
    fotoPerfil: String?,
    estadoVerificacionCI: Boolean,
    ci: String,
    fotoCI: String?,
    fotoBiometricaFacial: String?,
    fechaRegistro: Date = Date()
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

    // Atributos derivados o listas de apoyo para la app
    val favoritos: MutableList<String> = mutableListOf()
    val historialCompras: MutableList<String> = mutableListOf()

    override fun registrarse(): Boolean {
        // Lógica de registro para el comprador en CYBER-QHATU
        return true
    }

    fun escanearQR() {
        // Lógica para abrir la cámara y escanear el QR fijo de la tienda o de pago
    }

    fun abrirChatWhatsapp(telefonoVendedor: String) {
        // Lógica para abrir la API de WhatsApp y contactar al feriante
    }

    fun reportarTienda(idTienda: String, motivo: String) {
        // Lógica para enviar un reporte al moderador sobre una tienda irregular en la feria
    }

    // Métodos adicionales útiles para la navegación del comprador
    fun buscarProducto(query: String) {
        TODO("Búsqueda en el catálogo de la Feria 16 de Julio")
    }

    fun guardarFavorito(idProducto: String) {
        agregarFavoritoProducto(idProducto)
    }

    fun confirmarRecepcion(idPedido: String) {
        TODO("Confirmar la entrega del producto en el punto de encuentro")
    }

    fun calificarVendedor(idVendedor: String, calificacion: Int, comentario: String?) {
        TODO("Dejar reseña y calificación al comerciante")
    }
}