package com.cyberqhatu.app.transaccion

enum class EstadoEnvio { PENDIENTE, DESPACHADO }

// Solo se instancia si Pedido.tipoEntrega == TipoEntrega.ENVIO
class Envio(
    val idEnvio: String,
    val idPedido: String,
    var empresaFlota: String,
    var departamentoDestino: String,
    var ciudadDestino: String,
    var direccionCompleta: String,
    var nombreReceptor: String,
    var telefonoReceptor: String,
    var baucherDespacho: String? = null,
    var numeroGuia: String? = null,
    var estado: EstadoEnvio = EstadoEnvio.PENDIENTE
) {
    fun subirBaucher(url: String) { TODO() }
    fun marcarDespachado() { TODO() }
}
