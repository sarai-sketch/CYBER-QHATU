package com.cyberqhatu.app.usuarios

import java.util.Date

abstract class Usuario(
    val idUsuario: String,
    var nombre: String,
    var telefono: String,
    var correo: String? = null,
    var ci: String,
    var estadoVerificacion: Boolean = false,
    var fotoPerfil: String? = null,
    val fechaRegistro: Date = Date(),
    var strikes: Int = 0
) {
    abstract fun registrarse()
    fun loginOTP() { TODO("Login con código OTP") }
    fun verificarTelefono() { TODO("Verificación de teléfono") }
    fun subirCI(urlCI: String) { TODO("Subir foto/documento de CI") }
    fun compartirPerfil() { TODO("Compartir perfil público") }
    fun actualizarPerfil() { TODO("Actualizar datos de perfil") }
    fun evaluarSuspension(): Boolean { TODO("Evaluar si el usuario debe suspenderse por strikes") }
}
