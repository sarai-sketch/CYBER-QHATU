package com.cyberqhatu.app.vendedor.ui

/**
 * Estado local independiente para la Pantalla 7: Publicar Producto (D1).
 * Mantiene desacoplados los campos del formulario de publicación sin depender
 * de modelos globales de otras capas del proyecto.
 */
data class PublicarProductoFormState(
    val titulo: String = "",
    val categoria: String = "",
    val precio: String = "",
    val stock: String = "1",
    val descripcion: String = "",
    val condicion: CondicionProducto = CondicionProducto.NUEVO,
    val imagenesUris: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val mensajeError: String? = null,
    val esExitoso: Boolean = false,
    val tituloError: String? = null,
    val precioError: String? = null,
    val stockError: String? = null
)

enum class CondicionProducto(val tituloMostrar: String) {
    NUEVO("Nuevo"),
    USADO_COMO_NUEVO("Usado - Como nuevo"),
    USADO_BUEN_ESTADO("Usado - Buen estado"),
    REACONDICIONADO("Reacondicionado")
}
