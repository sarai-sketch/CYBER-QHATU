package com.cyberqhatu.app.feature.perfil.data.repository

import android.util.Log
import com.cyberqhatu.app.core.network.SqliteCloudClient
import com.cyberqhatu.app.core.network.SqliteCloudQueryRequest
import com.cyberqhatu.app.usuarios.Comprador
import com.cyberqhatu.app.usuarios.EstadoVerificacionCI
import com.cyberqhatu.app.usuarios.Usuario
import com.cyberqhatu.app.usuarios.Vendedor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class SqliteCloudUsuarioRepository {

    private val apiService = SqliteCloudClient.apiService

    fun obtenerUsuarioPorId(idUsuario: String): Flow<Usuario?> = flow {
        try {
            val sql = "SELECT id_usuario, nombre, correo, telefono_whatsapp, ci, foto_perfil, rol, estado_verificacion_ci FROM usuarios WHERE id_usuario = '$idUsuario' LIMIT 1;"
            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))

            if (response.isSuccessful && !response.body()?.data.isNullOrEmpty()) {
                val row = response.body()!!.data!!.first()
                val id = row["id_usuario"]?.toString() ?: idUsuario
                val nombre = row["nombre"]?.toString() ?: "Usuario Qhatu"
                val correo = row["correo"]?.toString() ?: "usuario@cyberqhatu.bo"
                val telefono = row["telefono_whatsapp"]?.toString() ?: "+591 70000000"
                val ci = row["ci"]?.toString()
                val foto = row["foto_perfil"]?.toString()
                val rol = row["rol"]?.toString() ?: "VENDEDOR"
                val estadoVerifStr = row["estado_verificacion_ci"]?.toString() ?: "PENDIENTE"

                val estadoVerif = if (estadoVerifStr == "VERIFICADO") EstadoVerificacionCI.VERIFICADO else EstadoVerificacionCI.PENDIENTE

                val usuario: Usuario = if (rol == "VENDEDOR") {
                    Vendedor(
                        idUsuario = id,
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
                        idUsuario = id,
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
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error consultando usuario en SQLite Cloud: ${e.message}", e)
            emit(null)
        }
    }

    suspend fun registrarOConsultarPorTelefono(telefono: String): Usuario {
        return try {
            val telefonoFormateado = if (telefono.startsWith("+591")) telefono else "+591 $telefono"
            val querySql = "SELECT id_usuario, nombre, correo, telefono_whatsapp, ci, foto_perfil, rol, estado_verificacion_ci FROM usuarios WHERE telefono_whatsapp = '$telefonoFormateado' LIMIT 1;"
            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = querySql))

            if (response.isSuccessful && !response.body()?.data.isNullOrEmpty()) {
                val row = response.body()!!.data!!.first()
                val id = row["id_usuario"]?.toString() ?: "USR-101"
                val nombre = row["nombre"]?.toString() ?: "Usuario Qhatu"
                val correo = row["correo"]?.toString() ?: "$id@cyberqhatu.bo"

                Vendedor(
                    idUsuario = id,
                    nombre = nombre,
                    correo = correo,
                    telefonoWhatsapp = telefonoFormateado,
                    estadoVerificacion = true
                )
            } else {
                val nuevoId = "USR-" + UUID.randomUUID().toString().take(6)
                val nuevoCorreo = "$nuevoId@cyberqhatu.bo"
                val insertSql = "INSERT INTO usuarios (id_usuario, nombre, correo, telefono_whatsapp, rol, estado_verificacion_ci) VALUES ('$nuevoId', 'Usuario $telefono', '$nuevoCorreo', '$telefonoFormateado', 'VENDEDOR', 'PENDIENTE');"
                apiService.ejecutarSql(SqliteCloudQueryRequest(sql = insertSql))

                Vendedor(
                    idUsuario = nuevoId,
                    nombre = "Usuario $telefono",
                    correo = nuevoCorreo,
                    telefonoWhatsapp = telefonoFormateado,
                    estadoVerificacion = false
                )
            }
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error al registrar usuario en SQLite Cloud: ${e.message}", e)
            Vendedor(
                idUsuario = "USR-101",
                nombre = "Usuario $telefono",
                correo = "usuario@cyberqhatu.bo",
                telefonoWhatsapp = "+591 $telefono",
                estadoVerificacion = true
            )
        }
    }

    suspend fun guardarOCrearUsuario(usuario: Usuario): Boolean {
        return try {
            val sql = "INSERT OR REPLACE INTO usuarios (id_usuario, nombre, correo, telefono_whatsapp, ci, foto_perfil, rol, estado_verificacion_ci) VALUES ('${usuario.idUsuario}', '${usuario.nombre.replace("'", "''")}', '${usuario.correo.replace("'", "''")}', '${usuario.telefonoWhatsapp}', '${usuario.ci ?: ""}', '${usuario.fotoPerfil ?: ""}', 'VENDEDOR', '${usuario.estadoVerificacionCI.name}');"
            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error al guardar usuario en SQLite Cloud: ${e.message}", e)
            false
        }
    }
}
