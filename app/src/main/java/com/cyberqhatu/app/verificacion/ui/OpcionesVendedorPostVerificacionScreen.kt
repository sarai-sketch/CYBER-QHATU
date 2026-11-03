package com.cyberqhatu.app.verificacion.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberqhatu.app.ui.theme.QhatuPurple
import com.cyberqhatu.app.vendedor.data.repository.SqliteCloudColaboradorRepository
import kotlinx.coroutines.launch

/**
 * Pantalla de opciones posteriores a la verificación biométrica exitosa:
 * 1. Crear una tienda (va al formulario).
 * 2. Ingresar código para administrar una tienda existente.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpcionesVendedorPostVerificacionScreen(
    currentUserEmail: String = "usuario@cyberqhatu.bo",
    onCrearTiendaClick: () -> Unit = {},
    onTiendaVinculada: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var mostrarDialogoCodigo by remember { mutableStateOf(false) }
    var codigoInv by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val colaboradorRepo = remember { SqliteCloudColaboradorRepository() }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Configuración de Vendedor", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "¡Identidad Verificada!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Has completado con éxito la verificación biométrica de tu carnet de identidad.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onCrearTiendaClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = QhatuPurple)
                ) {
                    Icon(Icons.Default.Store, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Crear una Tienda", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { mostrarDialogoCodigo = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("Ingresar código para administrar tienda", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                }
            }

            if (mostrarDialogoCodigo) {
                AlertDialog(
                    onDismissRequest = { mostrarDialogoCodigo = false },
                    title = { Text("Código de Administrador") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Ingresa el código de invitación provisto por el propietario de la tienda:", fontSize = 13.sp)
                            OutlinedTextField(
                                value = codigoInv,
                                onValueChange = { codigoInv = it },
                                label = { Text("Código de Invitación") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (codigoInv.isNotBlank()) {
                                    coroutineScope.launch {
                                        try {
                                            colaboradorRepo.aceptar(codigoInv, currentUserEmail)
                                            mostrarDialogoCodigo = false
                                            onTiendaVinculada()
                                        } catch (e: Exception) {
                                            snackbarHostState.showSnackbar("Error al vincular: ${e.message}")
                                        }
                                    }
                                }
                            },
                            enabled = codigoInv.isNotBlank()
                        ) {
                            Text("Vincular Tienda")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarDialogoCodigo = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}
