package com.cyberqhatu.app.tienda

enum class EstadoProducto { PENDIENTE_REVISION, ACTIVO, RESERVADO, VENDIDO, RECHAZADO }

class Producto(
    val idProducto: String,
    val idVendedor: String,
    val idTienda: String? = null,
    var nombre: String,
    var descripcion: String,
    var precio: Double,
    var stock: Int,
    var stockReservado: Int = 0,
    var estado: EstadoProducto = EstadoProducto.PENDIENTE_REVISION
) {
    fun actualizar() { TODO() }
    fun marcarVendido() { TODO() }
    fun compartir() { TODO() }
}
