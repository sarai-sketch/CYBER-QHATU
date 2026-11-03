package com.cyberqhatu.app.vendedor.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para enviar la solicitud de guardar producto a la API de SQLite Cloud.
 * Endpoint: https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/guardar_producto
 */
data class GuardarProductoRequestDto(
    @SerializedName("id_producto") val idProducto: String? = null,
    @SerializedName("id_tienda") val idTienda: String,
    @SerializedName("id_categoria") val idCategoria: String? = null,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("precio") val precio: Double,
    @SerializedName("stock") val stock: Int
)

/**
 * DTO de respuesta genérica desde SQLite Cloud.
 */
data class GuardarProductoResponseDto(
    @SerializedName("status") val status: String? = null,
    @SerializedName("success") val success: Boolean = true,
    @SerializedName("message") val message: String? = null,
    @SerializedName("error") val error: String? = null,
    @SerializedName("id_producto") val idProducto: String? = null
)

/**
 * DTO para eliminar producto.
 * Endpoint: https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/eliminar_producto
 */
data class EliminarProductoRequestDto(
    @SerializedName("id_producto") val idProducto: String
)

/**
 * DTO para ocultar / cambiar estado de producto.
 * Endpoint: https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/ocultar_producto
 */
data class OcultarProductoRequestDto(
    @SerializedName("id_producto") val idProducto: String,
    @SerializedName("estado") val estado: String = "INACTIVO"
)

/**
 * DTO para reabastecimiento / entrada de inventario.
 * Endpoint: https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/guardar_entrada_inventario
 */
data class EntradaInventarioRequestDto(
    @SerializedName("id_producto") val idProducto: String,
    @SerializedName("id_tienda") val idTienda: String,
    @SerializedName("cantidad") val cantidad: Int,
    @SerializedName("motivo") val motivo: String = "REABASTECIMIENTO"
)

/**
 * DTO para ajuste de inventario.
 * Endpoint: https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/guardar_ajuste_inventario
 */
data class AjusteInventarioRequestDto(
    @SerializedName("id_producto") val idProducto: String,
    @SerializedName("id_tienda") val idTienda: String,
    @SerializedName("nuevo_stock") val nuevoStock: Int,
    @SerializedName("motivo") val motivo: String
)

/**
 * DTO para registrar una venta.
 * Endpoint: https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/guardar_venta
 */
data class GuardarVentaRequestDto(
    @SerializedName("id_venta") val idVenta: String? = null,
    @SerializedName("id_tienda") val idTienda: String,
    @SerializedName("id_producto") val idProducto: String,
    @SerializedName("cantidad") val cantidad: Int,
    @SerializedName("precio_unitario") val precioUnitario: Double,
    @SerializedName("canal_venta") val canalVenta: String = "FISICA"
)

/**
 * DTO para eliminar / anular una venta.
 * Endpoint: https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/eliminar_venta
 */
data class EliminarVentaRequestDto(
    @SerializedName("id_venta") val idVenta: String
)

/**
 * DTO genérico de respuesta.
 */
data class ApiResponseDto(
    @SerializedName("status") val status: String? = null,
    @SerializedName("success") val success: Boolean = true,
    @SerializedName("message") val message: String? = null,
    @SerializedName("error") val error: String? = null
)
