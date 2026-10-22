package com.cyberqhatu.app.data.api

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cyberqhatu.app.data.local.dao.UserServerDao
import com.cyberqhatu.app.data.local.entity.UserServerEntity
import com.cyberqhatu.app.data.model.AuthResponse
import com.cyberqhatu.app.data.model.BaseResponse
import com.cyberqhatu.app.data.model.LoginRequest
import com.cyberqhatu.app.data.model.RegisterRequest
import com.cyberqhatu.app.data.model.ResendCodeRequest
import com.cyberqhatu.app.data.model.User
import com.cyberqhatu.app.data.model.VerifyEmailRequest
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.util.UUID

class MockAuthApiServiceImpl(
    private val context: Context,
    private val userServerDao: UserServerDao
) : AuthApiService {

    override suspend fun login(request: LoginRequest): Response<AuthResponse> {
        delay(1000) // Simular red
        val user = userServerDao.getUserByEmail(request.email)
        return if (user != null && user.passwordHash == request.password) {
            if (!user.isEmailVerified) {
                // Devolver código 403 Forbidden para indicar que no está verificado
                Response.error(403, "Email no verificado".toResponseBody(null))
            } else {
                val authResponse = AuthResponse(
                    token = "mock-token-${UUID.randomUUID()}",
                    user = User(
                        id = "usr-${user.email}",
                        email = user.email,
                        name = user.name
                    )
                )
                Response.success(authResponse)
            }
        } else {
            Response.error(401, okhttp3.ResponseBody.create(null, "Credenciales incorrectas"))
        }
    }

    override suspend fun register(request: RegisterRequest): Response<BaseResponse> {
        delay(1000)
        val existing = userServerDao.getUserByEmail(request.email)
        if (existing != null) {
            return Response.error(400, okhttp3.ResponseBody.create(null, "El correo ya está registrado"))
        }

        val otpCode = (100000..999999).random().toString()
        val expiration = System.currentTimeMillis() + (15 * 60 * 1000) // 15 minutos

        val newUser = UserServerEntity(
            email = request.email,
            passwordHash = request.password,
            name = request.name,
            isEmailVerified = false,
            otpCode = otpCode,
            otpExpiration = expiration
        )
        userServerDao.insertUser(newUser)

        simularEnvioCorreo(request.email, otpCode)

        return Response.success(BaseResponse("Registro exitoso. Se ha enviado un código de verificación al correo."))
    }

    override suspend fun verifyEmail(request: VerifyEmailRequest): Response<BaseResponse> {
        delay(1000)
        val user = userServerDao.getUserByEmail(request.email) ?: return Response.error(404, okhttp3.ResponseBody.create(null, "Usuario no encontrado"))

        if (user.isEmailVerified) {
            return Response.error(400, okhttp3.ResponseBody.create(null, "El correo ya está verificado"))
        }

        if (user.otpCode == null || user.otpExpiration == null || System.currentTimeMillis() > user.otpExpiration) {
            return Response.error(400, okhttp3.ResponseBody.create(null, "Código expirado o inválido. Solicita uno nuevo."))
        }

        if (user.otpCode != request.code) {
            return Response.error(400, okhttp3.ResponseBody.create(null, "Código incorrecto"))
        }

        // Verificado correctamente
        val updatedUser = user.copy(isEmailVerified = true, otpCode = null, otpExpiration = null)
        userServerDao.updateUser(updatedUser)

        return Response.success(BaseResponse("Correo verificado correctamente. Ya puedes iniciar sesión."))
    }

    override suspend fun resendCode(request: ResendCodeRequest): Response<BaseResponse> {
        delay(1000)
        val user = userServerDao.getUserByEmail(request.email) ?: return Response.error(404, okhttp3.ResponseBody.create(null, "Usuario no encontrado"))

        if (user.isEmailVerified) {
            return Response.error(400, okhttp3.ResponseBody.create(null, "El correo ya está verificado"))
        }

        val otpCode = (100000..999999).random().toString()
        val expiration = System.currentTimeMillis() + (15 * 60 * 1000) // 15 minutos

        val updatedUser = user.copy(otpCode = otpCode, otpExpiration = expiration)
        userServerDao.updateUser(updatedUser)

        simularEnvioCorreo(request.email, otpCode)

        return Response.success(BaseResponse("Se ha reenviado un nuevo código de verificación."))
    }

    private fun simularEnvioCorreo(email: String, codigo: String) {
        Log.d("FAKE_API", "Enviando código $codigo al correo $email")

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "auth_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones de Autenticación",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Simula correos del sistema"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle("Código de Verificación - CyberQhatu")
            .setContentText("Tu código es: $codigo")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify((System.currentTimeMillis() % 10000).toInt(), builder.build())
    }
}
