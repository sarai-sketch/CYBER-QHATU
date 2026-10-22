package com.cyberqhatu.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.cyberqhatu.app.ui.viewmodel.AuthState

@Composable
fun PantallaConfirmacionOTP(
    numeroTelefono: String = "+591 7XXXXXXX",
    onConfirmarClicked: (String) -> Unit = {},
    onReenviarClicked: () -> Unit = {},
    authState: AuthState = AuthState.Idle,
    onSuccess: () -> Unit = {}
) {
    var codigoOtp by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        if (authState is AuthState.VerifySuccess) {
            onSuccess()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono de mensaje / verificación
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.MarkEmailRead,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Verifica tu código",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Hemos enviado un código de confirmación por WhatsApp al $numeroTelefono",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo para el código OTP (6 dígitos)
            OutlinedTextField(
                value = codigoOtp,
                onValueChange = { input ->
                    if (input.all { it.isDigit() } && input.length <= 6) {
                        codigoOtp = input
                    }
                },
                label = { Text("Código de 6 dígitos") },
                placeholder = { Text("123456") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón principal de confirmación
            Button(
                onClick = { onConfirmarClicked(codigoOtp) },
                enabled = codigoOtp.length == 6 && authState !is AuthState.Loading,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Confirmar e Ingresar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (authState) {
                is AuthState.Error -> {
                    Text("Error: ${authState.message}", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                is AuthState.ResendSuccess -> {
                    Text(authState.message, color = Color(0xFF4CAF50))
                    Spacer(modifier = Modifier.height(8.dp))
                }
                else -> {}
            }

            // Opción visual para reenviar código
            TextButton(onClick = onReenviarClicked) {
                Text(
                    text = "¿No recibiste el código? Reenviar en 30s",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}