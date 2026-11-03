package com.cyberqhatu.app.auth.data.repository

import com.cyberqhatu.app.auth.domain.repository.AuthRepository
import com.cyberqhatu.app.core.network.SqliteCloudClient
import com.cyberqhatu.app.core.network.SqliteCloudQueryRequest
import com.cyberqhatu.app.core.network.parseAsListOfMaps
import com.cyberqhatu.app.feature.perfil.data.repository.SqliteCloudUsuarioRepository
import com.cyberqhatu.app.usuarios.Comprador
import com.cyberqhatu.app.usuarios.EstadoVerificacionCI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AuthRepositoryImpl : AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val usuarioRepository = SqliteCloudUsuarioRepository()

    override fun isUserLoggedInAndVerified(): Boolean {
        val user = auth.currentUser
        return user != null
    }

    override fun getCurrentUserEmail(): String? = auth.currentUser?.email

    override fun getCurrentUserName(): String? = auth.currentUser?.displayName

    override fun logout() {
        auth.signOut()
    }

    override suspend fun login(email: String, contrasena: String): Result<Pair<Boolean, String>> = suspendCancellableCoroutine { continuation ->
        auth.signInWithEmailAndPassword(email, contrasena)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    GlobalScope.launch {
                        var telefono = "+591 70000000"
                        try {
                            val sql = "SELECT telefono_whatsapp FROM usuarios WHERE correo = '$email' OR id_usuario = '$email' LIMIT 1;"
                            val response = SqliteCloudClient.apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))
                            val dataList = response.body()?.data.parseAsListOfMaps()
                            if (response.isSuccessful && dataList.isNotEmpty()) {
                                val telRemoto = dataList.first()["telefono_whatsapp"]?.toString()
                                if (!telRemoto.isNullOrBlank()) {
                                    telefono = telRemoto
                                }
                            }
                        } catch (_: Exception) {}
                        continuation.resume(Result.success(Pair(true, telefono)))
                    }
                } else {
                    continuation.resume(Result.failure(task.exception ?: Exception("Contraseña o correo incorrectos")))
                }
            }
    }

    override suspend fun register(nombre: String, email: String, telefono: String, contrasena: String): Result<String> = suspendCancellableCoroutine { continuation ->
        auth.createUserWithEmailAndPassword(email, contrasena)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(nombre)
                        .build()
                    
                    user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                        GlobalScope.launch {
                            try {
                                usuarioRepository.guardarOCrearUsuario(
                                    Comprador(
                                        idUsuario = email,
                                        nombre = nombre,
                                        correo = email,
                                        telefonoWhatsapp = telefono,
                                        estadoVerificacionCI = EstadoVerificacionCI.PENDIENTE
                                    )
                                )
                            } catch (_: Exception) {}
                        }
                        continuation.resume(Result.success(telefono))
                    } ?: continuation.resume(Result.success(telefono))
                } else {
                    continuation.resume(Result.failure(task.exception ?: Exception("Error al registrar cuenta")))
                }
            }
    }

    override suspend fun updateProfileName(newName: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val user = auth.currentUser
        if (user != null && newName.isNotBlank()) {
            val updates = UserProfileChangeRequest.Builder()
                .setDisplayName(newName.trim())
                .build()
            
            user.updateProfile(updates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(Result.success(Unit))
                } else {
                    continuation.resume(Result.failure(task.exception ?: Exception("Error al actualizar perfil")))
                }
            }
        } else {
            continuation.resume(Result.failure(Exception("Usuario no válido o nombre vacío")))
        }
    }
}
