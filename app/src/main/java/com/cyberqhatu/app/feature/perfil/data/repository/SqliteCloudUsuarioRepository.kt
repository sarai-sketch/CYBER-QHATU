package com.cyberqhatu.app.feature.perfil.data.repository

import com.cyberqhatu.app.core.network.SqliteCloudClient
import com.cyberqhatu.app.core.network.SqliteCloudQueryRequest
import com.cyberqhatu.app.usuarios.Comprador
import com.cyberqhatu.app.usuarios.EstadoVerificacionCI
import com.cyberqhatu.app.usuarios.Usuario
import com.cyberqhatu.app.usuarios.Vendedor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SqliteCloudUsuarioRepository {

    private val apiService = SqliteCloudClient.apiService

    fun obtenerUsuarioPorId(idUsuario: String): Flow<Usuario?> = flow {
        try {
            val response = apiService.ejecutarSql(
                SqliteCloudQueryRequest(
                    sql = "SELECT id_usuario, nombre, correo, telefono_whatsapp, ci, foto_perfil, rol, estado_verificacion_ci FROM usuarios WHERE id_usuario = '$idUsuario' LIMIT 1;"
                )
            )
            if (response.isSuccessful && !response.body()?.data.isNullOrEmpty()) {
                val row = response.body()!!.data!!.first()
                val nombre = row["nombre"]?.toString() ?: "Usuario"
                val correo = row["correo"]?.toString() ?: ""
                val telefono = row["telefono_whatsapp"]?.toString() ?: ""
                val ci = row["ci"]?.toString()
                val foto = row["foto_perfil"]?.toString()
                val rol = row["rol"]?.toString() ?: "VENDEDOR"
                val estadoVerifStr = row["estado_verificacion_ci"]?.toString() ?: "PENDIENTE"

                val estadoVerif = if (estadoVerifStr == "VERIFICADO") EstadoVerificacionCI.VERIFICADO else EstadoVerificacionCI.PENDIENTE

                val usuario: Usuario = if (rol == "VENDEDOR") {
                    Vendedor(
                        idUsuario = idUsuario,
                        nombre = nombre,
                        correo = correo,
                        telefonoWhatsapp = telefono,
                        fotoPerfil = foto,
                        estadoVerificacionCI = estadoVerif,
                        ci = ci,
                        estadoVerificacion = estadoVerif == EstadoVerificacionCI.VERIFICADO
                    )
                } else {
                    Comprador(
                        idUsuario = idUsuario,
                        nombre = nombre,
                        correo = correo,
                        telefonoWhatsapp = telefono,
                        fotoPerfil = foto,
                        estadoVerificacionCI = estadoVerif,
                        ci = ci
                    )
                }
                emit(usuario)
            } else {
                emit(
                    Vendedor(
                        idUsuario = idUsuario,
                        nombre = "Juan Pérez",
                        correo = "juan.perez@cyberqhatu.bo",
                        telefonoWhatsapp = "+591 70000000",
                        estadoVerificacion = true
                    )
                )
            }
        } catch (_: Exception) {
            emit(null)
        }
    }

    suspend fun guardarOCrearUsuario(usuario: Usuario): Boolean {
        return try {
            val sql = "INSERT OR REPLACE INTO usuarios (id_usuario, nombre, correo, telefono_whatsapp, ci, foto_perfil, rol, estado_verificacion_ci) VALUES ('${usuario.idUsuario}', '${usuario.nombre.replace("'", "''")}', '${usuario.correo.replace("'", "''")}', '${usuario.telefonoWhatsapp}', '${usuario.ci ?: ""}', '${usuario.fotoPerfil ?: ""}', 'VENDEDOR', '${usuario.estadoVerificacionCI.name}');"
            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))
            response.isSuccessful
        } catch (_: Exception) {
            false
        }
    }
}
