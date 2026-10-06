package com.cyberqhatu.app.usuarios

import java.util.Date

class Moderador(
    idUsuario: String,
    nombre: String,
    correo: String,
    telefonoWhatsapp: String,
    fotoPerfil: String?,
    estadoVerificacionCI: Boolean,
    ci: String,
    fotoCI: String?,
    fotoBiometricaFacial: String?,
    fechaRegistro: Date = Date()
) : Usuario(
    idUsuario = idUsuario,
    nombre = nombre,
    correo = correo,
    telefonoWhatsapp = telefonoWhatsapp,
    fotoPerfil = fotoPerfil,
    estadoVerificacionCI = estadoVerificacionCI,
    ci = ci,
    fotoCI = fotoCI,
    fotoBiometricaFacial = fotoBiometricaFacial,
    fechaRegistro = fechaRegistro
) {

    override fun registrarse(): Boolean {
        // Lógica de registro para el moderador en el sistema
        return true
    }

    fun aprobarVerificacionVendedor(vendedor: Vendedor) {
        // Valida la documentación (CI y biométrica facial) y aprueba al vendedor
        vendedor.estadoVerificacion = true
        vendedor.estadoVerificacionCI = true
    }

    fun revisarReportes() {
        // Consulta y lista los reportes emitidos por los compradores sobre tiendas o productos irregulares
        TODO("Lógica para consultar reportes pendientes")
    }

    fun resolverReporte(idReporte: String, aplicarSuspension: Boolean) {
        // Toma una medida disciplinaria, como sancionar o suspender una tienda si acumula strikes
        TODO("Lógica para resolver el reporte y actualizar estado")
    }

    // Métodos auxiliares adicionales de administración
    fun bloquearUsuario(idUsuario: String) {
        TODO("Bloquear acceso a usuarios con conductas infractoras")
    }
}