package com.cyberqhatu.app.transaccion

import java.util.Date

enum class TipoEntrega { RECOJO_EN_TIENDA, PUNTO_ENCUENTRO, ENVIO }
enum class EstadoPedido { PENDIENTE, ACORDADO, DESPACHADO_LISTO_PARA_RECOJO, ENTREGADO, CANCELADO }

class Pedido(
    val idPedido: String,
    val idComprador: String,
    val idVendedor: String,
    val idProducto: String,
    var idRegistro: String? = null,
    var tipoEntrega: TipoEntrega,
    var estado: EstadoPedido = EstadoPedido.PENDIENTE,
    var precio: Double,
    val fechaCreacion: Date = Date()
) {
    fun crear() { TODO() }
    fun cancelar() { TODO() }
    fun confirmarEntrega() { TODO() }
}
