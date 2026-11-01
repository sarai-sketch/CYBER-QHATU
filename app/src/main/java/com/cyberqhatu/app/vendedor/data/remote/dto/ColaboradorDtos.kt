package com.cyberqhatu.app.vendedor.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para agregar / invitar colaborador a una tienda.
 * Endpoint: https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/guardar_colaborador
 */
data class GuardarColaboradorRequestDto(
    @SerializedName("id_tienda") val idTienda: String = "t1",
    @SerializedName("correo_colaborador") val correoColaborador: String,
    @SerializedName("rol") val rol: String = "ADMINISTRADOR_COLABORADOR"
)

/**
 * DTO de respuesta al invitar un colaborador.
 */
data class GuardarColaboradorResponseDto(
    @SerializedName("status") val status: String? = null,
    @SerializedName("success") val success: Boolean = true,
    @SerializedName("message") val message: String? = null,
    @SerializedName("codigo_invitacion") val codigoInvitacion: String? = null
)

/**
 * DTO para aceptar invitación con código.
 * Endpoint: https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/aceptar_invitacion
 */
data class AceptarInvitacionRequestDto(
    @SerializedName("codigo_invitacion") val codigoInvitacion: String,
    @SerializedName("id_usuario") val idUsuario: String
)

/**
 * DTO para eliminar / dar de baja colaborador.
 * Endpoint: https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/eliminar_colaborador
 */
data class EliminarColaboradorRequestDto(
    @SerializedName("id_tienda") val idTienda: String = "t1",
    @SerializedName("id_colaborador") val idColaborador: String
)

/**
 * DTO para eliminar / cancelar invitación pendiente.
 * Endpoint: https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/eliminar_invitacion
 */
data class EliminarInvitacionRequestDto(
    @SerializedName("id_invitacion") val idInvitacion: String
)

/**
 * DTO para cambiar rol de colaborador en la tienda.
 * Endpoint: https://clxgbyqcvz.g6.gateway.sqlite.cloud/v2/functions/cambiar_rol_colaborador
 */
data class CambiarRolColaboradorRequestDto(
    @SerializedName("id_tienda") val idTienda: String = "t1",
    @SerializedName("id_colaborador") val idColaborador: String,
    @SerializedName("nuevo_rol") val nuevoRol: String
)
