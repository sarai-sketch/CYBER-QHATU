package com.cyberqhatu.app.ui.screens.vendedor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberqhatu.app.ui.theme.QhatuPurple

data class Colaborador(val id: String, val nombre: String, val rol: String)

/**
 * Pantalla dedicada para que el Administrador de la Tienda agregue y gestione más administradores colaboradores.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionColaboradoresScreen(
    onBackClick: () -> Unit = {}
) {
    var colaboradores by remember {
        mutableStateOf(
            listOf(
                Colaborador("1", "Carlos Mendoza", "Administrador Principal"),
                Colaborador("2", "Ana Condori", "Administrador Colaborador")
            )
        )
    }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var nuevoNombre by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Administradores de Tienda", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogo = true },
                containerColor = QhatuPurple,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Agregar Administrador")
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
                Text("Los administradores colaboradores tienen las mismas funciones para gestionar inventario y productos.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

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
                                        colaboradores = colaboradores.filter { it.id != colab.id }
                                    }) {
                                        Text("Remover", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (mostrarDialogo) {
                AlertDialog(
                    onDismissRequest = { mostrarDialogo = false },
                    title = { Text("Agregar Administrador") },
                    text = {
                        OutlinedTextField(
                            value = nuevoNombre,
                            onValueChange = { nuevoNombre = it },
                            label = { Text("Nombre del colaborador") },
                            shape = RoundedCornerShape(10.dp)
                        )
                    },
                    confirmButton = {
                        Button(onClick = {
                            if (nuevoNombre.isNotBlank()) {
                                colaboradores = colaboradores + Colaborador(System.currentTimeMillis().toString(), nuevoNombre, "Administrador Colaborador")
                                nuevoNombre = ""
                                mostrarDialogo = false
                            }
                        }) {
                            Text("Agregar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarDialogo = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}
