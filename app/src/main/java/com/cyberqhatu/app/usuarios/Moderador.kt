package com.cyberqhatu.app.usuarios

class Moderador(
    idUsuario: String,
    nombre: String,
    telefono: String,
    correo: String?,
    ci: String
) : Usuario(idUsuario, nombre, telefono, correo, ci) {

    override fun registrarse() { TODO() }
    fun bloquearUsuario(idUsuario: String) { TODO() }
    fun validarUsuario(idUsuario: String) { TODO() }
    fun revisarProducto(idProducto: String) { TODO() }
    fun revisarReportes() { TODO() }
    fun resolverReporte(idReporte: String) { TODO() }
}
