package com.cyberqhatu.app.vendedor.ui

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
