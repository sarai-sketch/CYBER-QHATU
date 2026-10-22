package com.cyberqhatu.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberqhatu.app.ui.theme.CyberQhatuTheme
import com.cyberqhatu.app.ui.theme.QhatuLime
import com.cyberqhatu.app.ui.theme.QhatuPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMiPerfil(
    nombreUsuario: String,
    correoUsuario: String,
    telefonoUsuario: String,
    estadoVerificacion: Boolean,
    tipoRol: String,
    onEditarPerfilClick: () -> Unit,
    onCerrarSesionClick: () -> Unit,
    onVerificarIdentidadClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    CyberQhatuTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Mi Perfil", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = QhatuPurple) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = QhatuPurple,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = QhatuPurple,
                        navigationIconContentColor = QhatuPurple,
                        actionIconContentColor = QhatuPurple,
                    ),
                )
            },
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = MaterialTheme.colorScheme.background,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // Avatar simulado del usuario
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = nombreUsuario.take(2).uppercase(),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nombre del usuario
                    Text(
                        text = nombreUsuario,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    // Rol actual
                    Text(
                        text = "Rol: $tipoRol",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tarjeta de información general
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Correo: $correoUsuario", fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "WhatsApp: $telefonoUsuario", fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (estadoVerificacion) "Estado CI: Verificado ✅" else "Estado CI: No verificado ⏳",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (estadoVerificacion) Color(0xFF2E7D32) else Color(0xFFC62828),
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onVerificarIdentidadClick,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = QhatuLime),
                            ) {
                                Text(
                                    text = if (estadoVerificacion) "Re-verificar Identidad" else "Verificar Identidad (C.I. y Rostro)",
                                    color = QhatuPurple,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botones de gestión del perfil
                    OutlinedButton(
                        onClick = onEditarPerfilClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(text = "Editar Perfil")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onCerrarSesionClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(text = "Cerrar Sesión")
                    }
                }
            }
        }
    }
}
