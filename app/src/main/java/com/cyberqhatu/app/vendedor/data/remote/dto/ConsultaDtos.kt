package com.cyberqhatu.app.vendedor.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para listar productos de una tienda.
 * Endpoint: https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/listar_productos
 */
data class ListarProductosRequestDto(
    @SerializedName("id_tienda") val idTienda: String = "t1",
    @SerializedName("id_usuario") val idUsuario: String = "USR-101"
)

/**
 * DTO para listar historial de movimientos de inventario.
 * Endpoint: https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/listar_historial_movimientos
 */
data class ListarMovimientosRequestDto(
    @SerializedName("id_tienda") val idTienda: String = "t1",
    @SerializedName("limite") val limite: Int = 100
)

/**
 * DTO para listar miembros e invitaciones pendientes.
 * Endpoint: https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/listar_colaboradores
 */
data class ListarColaboradoresRequestDto(
    @SerializedName("id_tienda") val idTienda: String = "t1"
)

/**
 * DTO para obtener estadísticas de ventas por rango.
 * Endpoint: https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/estadisticas_ventas
 */
data class EstadisticasVentasRequestDto(
    @SerializedName("id_tienda") val idTienda: String = "t1",
    @SerializedName("rango_tiempo") val rangoTiempo: String = "-30 days"
)

/**
 * DTO de respuesta para estadísticas de ventas.
 */
data class EstadisticasVentasResponseDto(
    @SerializedName("total_ventas") val totalVentas: Int = 0,
    @SerializedName("unidades_vendidas") val unidadesVendidas: Int = 0,
    @SerializedName("ingresos_totales") val ingresosTotales: Double = 0.0,
    @SerializedName("ganancia_estimada") val gananciaEstimada: Double = 0.0
)
