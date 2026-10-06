package com.cyberqhatu.app.tienda

enum class EstadoProducto {
    PENDIENTE_REVISION,
    ACTIVO,
    OCULTO,
    ELIMINADO
}

class Producto(
    val idProducto: String,
    val idTienda: String,
    val idCategoria: String,
    var marca: String? = null,
    var modelo: String? = null,
    var nombre: String,
    var descripcion: String,
    var precio: Double,
    var precioCompra: Double? = null,
    var stock: Int,
    var stockMinimo: Int? = null,
    var estado: EstadoProducto = EstadoProducto.PENDIENTE_REVISION,
    var etiquetaPrecioTransparente: Boolean = false
) {
    fun publicar() {
    }

    fun editar() {
    }

    fun eliminar() {
    }

    fun obtenerLinkWhatsappDirecto(): String {
        return ""
    }
}
