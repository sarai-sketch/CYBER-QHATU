package com.cyberqhatu.app.transaccion

interface MetodoPago {
    fun validarDatos(): Boolean
    fun procesar(monto: Double)
}

class PagoQR(
    var comprobanteImg: String? = null
) : MetodoPago {
    fun subirComprobante(url: String) { TODO() }
    override fun validarDatos(): Boolean { TODO() }
    override fun procesar(monto: Double) { TODO() }
}

class PagoEfectivo : MetodoPago {
    override fun validarDatos(): Boolean { TODO() }
    override fun procesar(monto: Double) { TODO() }
}
