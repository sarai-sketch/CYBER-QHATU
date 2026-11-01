package com.cyberqhatu.app.ui.screens.vendedor

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberqhatu.app.ui.theme.QhatuPurple
import com.cyberqhatu.app.vendedor.data.repository.TiendaColaboradorRepositoryImpl
import kotlinx.coroutines.launch

data class Colaborador(val id: String, val nombre: String, val rol: String)

/**
 * Pantalla dedicada para que el Administrador de la Tienda agregue y gestione administradores colaboradores,
 * generando y compartiendo el código de invitación vía WhatsApp / Android Share Chooser.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionColaboradoresScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { TiendaColaboradorRepositoryImpl() }

    var colaboradores by remember {
        mutableStateOf(
            listOf(
                Colaborador("1", "Carlos Mendoza", "Administrador Principal"),
                Colaborador("2", "Ana Condori", "Administrador Colaborador")
            )
        )
    }
    var mostrarDialogoInvitar by remember { mutableStateOf(false) }
    var mostrarDialogoAceptar by remember { mutableStateOf(false) }

    var correoColaborador by remember { mutableStateOf("") }
    var codigoParaAceptar by remember { mutableStateOf("") }
    var codigoGenerado by remember { mutableStateOf<String?>(null) }

    // Función para abrir el menú de compartir de Android (WhatsApp, etc.)
    fun compartirCodigo(codigo: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                "Te invito a colaborar en mi tienda en CyberQhatu. Tu código de invitación es: $codigo"
            )
        }
        context.startActivity(Intent.createChooser(intent, "Compartir invitación"))
    }

    LaunchedEffect(codigoGenerado) {
        codigoGenerado?.let { codigo ->
            compartirCodigo(codigo)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Administradores de Tienda", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    TextButton(onClick = { mostrarDialogoAceptar = true }) {
                        Text("Ingresar Código", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogoInvitar = true },
                containerColor = QhatuPurple,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Invitar Colaborador")
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text("Equipo de Administración", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(4.dp))
                Text("Los administradores colaboradores tienen acceso a inventario y productos mediante un código de invitación.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(Modifier.height(16.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(colaboradores) { colab ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(colab.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Spacer(Modifier.height(2.dp))
                                    Text(colab.rol, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                }
                                if (colab.rol != "Administrador Principal") {
                                    TextButton(onClick = {
                                        coroutineScope.launch {
                                            repository.eliminarColaborador(idColaborador = colab.id)
                                            colaboradores = colaboradores.filter { it.id != colab.id }
                                        }
                                    }) {
                                        Text("Remover", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Diálogo para Invitar Colaborador (Llama a guardar_colaborador y genera código)
            if (mostrarDialogoInvitar) {
                AlertDialog(
                    onDismissRequest = { mostrarDialogoInvitar = false },
                    title = { Text("Invitar Colaborador") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Ingresa el correo del colaborador para generar su código de invitación:", fontSize = 13.sp)
                            OutlinedTextField(
                                value = correoColaborador,
                                onValueChange = { correoColaborador = it },
                                label = { Text("Correo electrónico") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (correoColaborador.isNotBlank()) {
                                    coroutineScope.launch {
                                        val codigoRespuesta = repository.guardarColaborador(
                                            idTienda = "t1",
                                            correoColaborador = correoColaborador.trim(),
                                            rol = "ADMINISTRADOR_COLABORADOR"
                                        )
                                        colaboradores = colaboradores + Colaborador(
                                            id = System.currentTimeMillis().toString(),
                                            nombre = correoColaborador.substringBefore("@"),
                                            rol = "Administrador Colaborador"
                                        )
                                        codigoGenerado = codigoRespuesta ?: "QHATU-9921"
                                        correoColaborador = ""
                                        mostrarDialogoInvitar = false
                                    }
                                }
                            },
                            enabled = correoColaborador.contains("@")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Invitar y Compartir")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarDialogoInvitar = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            // Diálogo para Aceptar Invitación con Código
            if (mostrarDialogoAceptar) {
                AlertDialog(
                    onDismissRequest = { mostrarDialogoAceptar = false },
                    title = { Text("Aceptar Invitación a Tienda") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Ingresa el código de invitación que te envió el propietario:", fontSize = 13.sp)
                            OutlinedTextField(
                                value = codigoParaAceptar,
                                onValueChange = { codigoParaAceptar = it },
                                label = { Text("Código de Invitación (Ej. K7P2QX)") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (codigoParaAceptar.isNotBlank()) {
                                    coroutineScope.launch {
                                        repository.aceptarInvitacion(
                                            codigoInvitacion = codigoParaAceptar.trim().uppercase(),
                                            idUsuario = "USR-101"
                                        )
                                        codigoParaAceptar = ""
                                        mostrarDialogoAceptar = false
                                    }
                                }
                            },
                            enabled = codigoParaAceptar.isNotBlank()
                        ) {
                            Text("Aceptar Código")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarDialogoAceptar = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}
