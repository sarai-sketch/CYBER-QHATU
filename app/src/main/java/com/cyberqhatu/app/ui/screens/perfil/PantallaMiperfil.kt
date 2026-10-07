package com.cyberqhatu.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaMiPerfil(
    nombreUsuario: String,
    correoUsuario: String,
    telefonoUsuario: String,
    estadoVerificacion: Boolean,
    tipoRol: String,
    onEditarPerfilClick: () -> Unit,
    onCerrarSesionClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Avatar simulado del usuario
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = nombreUsuario.take(2).uppercase(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre del usuario
            Text(
                text = nombreUsuario,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            // Rol actual
            Text(
                text = "Rol: $tipoRol",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta de información general
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Correo: $correoUsuario", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "WhatsApp: $telefonoUsuario", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (estadoVerificacion) "Estado CI: Verificado " else "Estado CI: Pendiente de aprobación ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (estadoVerificacion) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botones de gestión del perfil
            OutlinedButton(
                onClick = onEditarPerfilClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Editar Perfil")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onCerrarSesionClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Cerrar Sesión")
            }
        }
    }
}