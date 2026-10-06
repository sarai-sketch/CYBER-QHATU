package com.cyberqhatu.app.tienda

enum class TipoTienda { FISICA, VIRTUAL, HIBRIDA }
enum class EstadoTienda { ACTIVA, SUSPENDIDA }

class Tienda(
    val idTienda: String,
    val idPropietario: String,
    var nombre: String,
    var descripcion: String,
    var logo: String?,
    var tipo: TipoTienda,
    var direccionGoogleMaps: String? = null,
    var horarioApertura: String? = null,
    var horarioCierre: String? = null,
    var linkWhatsapp: String? = null,
    var qrFijoTienda: String? = null,
    var strikes: Int = 0,
    var estado: EstadoTienda = EstadoTienda.ACTIVA
) {

    fun crearInvitacion(correoDestinatario: String, rolAsignado: String): Any {
        // Lógica para generar la invitación a un nuevo miembro o administrador de la tienda
        return "Invitación creada para $correoDestinatario"
    }

    fun generarCodigoVentaVirtual(): String {
        // Genera un código único para transacciones virtuales en la feria
        return "QHATU-${idTienda}-${System.currentTimeMillis().toString().takeLast(4)}"
    }

    fun calcularPromedioResenas(): Double {
        // Calcula el promedio de estrellas basado en las calificaciones de los compradores
        return 0.0
    }

    fun evaluarSuspension() {
        // Evalúa si la tienda supera el límite de faltas para cambiar su estado a suspendida
        if (strikes >= 3) {
            estado = EstadoTienda.SUSPENDIDA
        }
    }

    fun actualizarInfo(nuevaDesc: String, nuevoHorarioA: String, nuevoHorarioC: String) {
        // Actualiza los datos informativos de la tienda
        descripcion = nuevaDesc
        horarioApertura = nuevoHorarioA
        horarioCierre = nuevoHorarioC
    }

    // Métodos auxiliares adicionales
    fun validarTienda() { TODO() }
    fun agregarFoto(url: String) { TODO() }
}