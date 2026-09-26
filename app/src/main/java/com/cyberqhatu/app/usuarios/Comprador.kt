package com.cyberqhatu.app.usuarios

class Comprador(
    idUsuario: String,
    nombre: String,
    telefono: String,
    correo: String?,
    ci: String
) : Usuario(idUsuario, nombre, telefono, correo, ci) {

    val favoritos: MutableList<String> = mutableListOf()        // ids de Producto (atributo derivado)
    val historialCompras: MutableList<String> = mutableListOf() // ids de Pedido (atributo derivado)

    override fun registrarse() { TODO() }
    fun buscarProducto(query: String) { TODO() }
    fun guardarFavorito(idProducto: String) { TODO() }
    fun enviarConsulta(mensaje: String) { TODO() }
    fun solicitarCompra(idProducto: String) { TODO() }
    fun confirmarRecepcion(idPedido: String) { TODO() }
    fun calificarVendedor(idVendedor: String, calificacion: Int, comentario: String?) { TODO() }
    fun reportarUsuario(idUsuarioReportado: String, motivo: String) { TODO() }
}
