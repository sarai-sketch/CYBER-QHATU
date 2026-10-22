package com.cyberqhatu.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberqhatu.app.ui.viewmodel.AuthState
import kotlinx.coroutines.delay

@Composable
fun PantallaConfirmacionOTP(
    email: String = "tu correo",
    authState: AuthState = AuthState.Idle,
    onYaVerifiqueClicked: () -> Unit = {},
    onReenviarClicked: () -> Unit = {},
    onVolverClicked: () -> Unit = {}
) {
    var segundosRestantes by remember { mutableIntStateOf(60) }
    var puedoReenviar by remember { mutableStateOf(false) }

    LaunchedEffect(segundosRestantes, puedoReenviar) {
        if (!puedoReenviar && segundosRestantes > 0) {
            delay(1000L)
            segundosRestantes -= 1
            if (segundosRestantes == 0) {
                puedoReenviar = true
            }
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
                text = "Revisa tu correo",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Hemos enviado un enlace de verificación a:\n$email",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onYaVerifiqueClicked,
                enabled = authState !is AuthState.Loading,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Ya verifiqué mi correo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    onReenviarClicked()
                    puedoReenviar = false
                    segundosRestantes = 60
                },
                enabled = puedoReenviar && authState !is AuthState.Loading,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = if (puedoReenviar) "Reenviar correo" else "Reenviar en $segundosRestantes s",
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onVolverClicked) {
                Text(
                    text = "Cambiar de correo / Volver",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (authState) {
                is AuthState.Error -> {
                    Text(
                        text = authState.message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
                is AuthState.EmailSent -> {
                    Text(
                        text = authState.message,
                        color = Color(0xFF4CAF50),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
                else -> {}
            }
        }
    }
}
