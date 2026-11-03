package com.cyberqhatu.app.feature.perfil.data.repository

import android.util.Log
import com.cyberqhatu.app.core.network.GuardarUsuarioRequest
import com.cyberqhatu.app.core.network.SqliteCloudClient
import com.cyberqhatu.app.core.network.SqliteCloudFunctionsClient
import com.cyberqhatu.app.core.network.SqliteCloudQueryRequest
import com.cyberqhatu.app.core.network.parseAsListOfMaps
import com.cyberqhatu.app.usuarios.Comprador
import com.cyberqhatu.app.usuarios.EstadoVerificacionCI
import com.cyberqhatu.app.usuarios.Usuario
import com.cyberqhatu.app.usuarios.Vendedor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class SqliteCloudUsuarioRepository {

    private val apiService = SqliteCloudClient.apiService
    private val functionsService = SqliteCloudFunctionsClient.apiService

    fun obtenerUsuarioPorId(idUsuario: String): Flow<Usuario?> = flow {
        try {
            val sql = "SELECT id_usuario, nombre, correo, telefono_whatsapp, ci, foto_perfil, tipo_rol, estado_verificacion_ci FROM usuarios WHERE id_usuario = '$idUsuario' OR correo = '$idUsuario' LIMIT 1;"
            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))

            val dataList = response.body()?.data.parseAsListOfMaps()
            if (response.isSuccessful && dataList.isNotEmpty()) {
                val row = dataList.first()
                val id = row["id_usuario"]?.toString() ?: idUsuario
                val nombre = row["nombre"]?.toString() ?: "Usuario Qhatu"
                val correo = row["correo"]?.toString() ?: "usuario@cyberqhatu.bo"
                val telefono = row["telefono_whatsapp"]?.toString() ?: "+591 70000000"
                val ci = row["ci"]?.toString()
                val foto = row["foto_perfil"]?.toString()
                val rol = row["tipo_rol"]?.toString() ?: row["rol"]?.toString() ?: "COMPRADOR"
                val estadoVerifRaw = row["estado_verificacion_ci"]?.toString() ?: "0"
                val estadoVerif = if (estadoVerifRaw == "1" || estadoVerifRaw.uppercase() == "VERIFICADO") EstadoVerificacionCI.VERIFICADO else EstadoVerificacionCI.PENDIENTE

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
                emit(null)
            }
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error consultando usuario en SQLite Cloud: ${e.message}", e)
            emit(null)
        }
    }

    suspend fun registrarOConsultarPorTelefono(telefono: String): Usuario {
        return try {
            val telefonoFormateado = if (telefono.startsWith("+591")) telefono else "+591 $telefono"
            val querySql = "SELECT id_usuario, nombre, correo, telefono_whatsapp, ci, foto_perfil, tipo_rol, estado_verificacion_ci FROM usuarios WHERE telefono_whatsapp = '$telefonoFormateado' LIMIT 1;"
            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = querySql))

            val dataList = response.body()?.data.parseAsListOfMaps()
            if (response.isSuccessful && dataList.isNotEmpty()) {
                val row = dataList.first()
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
                val insertSql = "INSERT INTO usuarios (id_usuario, nombre, correo, telefono_whatsapp, tipo_rol, estado_verificacion_ci) VALUES ('$nuevoId', 'Usuario $telefono', '$nuevoCorreo', '$telefonoFormateado', 'COMPRADOR', 0);"
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
            val request = GuardarUsuarioRequest(
                idUsuario = usuario.idUsuario,
                nombre = usuario.nombre,
                correo = usuario.correo,
                telefono = usuario.telefonoWhatsapp
            )
            // Intentar primero vía Edge Function "guardar_usuario"
            val response = functionsService.guardarUsuario(request)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                Log.e("SqliteCloud", "Error en guardar_usuario Edge Function: HTTP ${response.code()} - $errorBody")

                val sql = "INSERT OR REPLACE INTO usuarios (id_usuario, nombre, correo, telefono_whatsapp, tipo_rol, estado_verificacion_ci) VALUES ('${usuario.idUsuario}', '${usuario.nombre.replace("'", "''")}', '${usuario.correo.replace("'", "''")}', '${usuario.telefonoWhatsapp}', 'COMPRADOR', ${if (usuario.estadoVerificacionCI == EstadoVerificacionCI.VERIFICADO) 1 else 0});"
                val sqlResponse = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))
                if (!sqlResponse.isSuccessful) {
                    Log.e("SqliteCloud", "Error en fallback SQL guardar usuario: HTTP ${sqlResponse.code()} - ${sqlResponse.errorBody()?.string()}")
                }
                return sqlResponse.isSuccessful
            }
            Log.d("SqliteCloud", "Usuario guardado exitosamente vía Edge Function guardar_usuario")
            true
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Excepción al guardar usuario en SQLite Cloud: ${e.message}", e)
            try {
                val sql = "INSERT OR REPLACE INTO usuarios (id_usuario, nombre, correo, telefono_whatsapp, tipo_rol, estado_verificacion_ci) VALUES ('${usuario.idUsuario}', '${usuario.nombre.replace("'", "''")}', '${usuario.correo.replace("'", "''")}', '${usuario.telefonoWhatsapp}', 'COMPRADOR', 0);"
                val sqlResponse = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))
                sqlResponse.isSuccessful
            } catch (ex: Exception) {
                Log.e("SqliteCloud", "Error en fallback SQL guardar usuario: ${ex.message}", ex)
                false
            }
        }
    }
}
