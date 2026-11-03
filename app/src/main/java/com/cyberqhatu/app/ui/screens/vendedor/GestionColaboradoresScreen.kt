package com.cyberqhatu.app.ui.screens.vendedor

import android.content.Intent
import android.util.Log
import android.widget.Toast
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
import com.cyberqhatu.app.core.session.SesionActual
import com.cyberqhatu.app.tienda.RolMiembro
import com.cyberqhatu.app.ui.theme.QhatuPurple
import com.cyberqhatu.app.vendedor.data.repository.MiembroUi
import com.cyberqhatu.app.vendedor.data.repository.SqliteCloudColaboradorRepository
import com.cyberqhatu.app.vendedor.data.repository.TipoMiembro
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "Colaboradores"

/**
 * Equipo de la tienda: propietario, miembros activos e invitaciones pendientes, todo desde SQLite Cloud.
 * Todas las llamadas a red van dentro de try/catch: un error se muestra en pantalla/Toast y nunca cierra la app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionColaboradoresScreen(
    storeId: String,
    onBackClick: () -> Unit = {},
    onInvitacionAceptada: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { SqliteCloudColaboradorRepository() }

    var miembros by remember { mutableStateOf<List<MiembroUi>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var errorCarga by remember { mutableStateOf<String?>(null) }
    var recargar by remember { mutableStateOf(0) }

    var mostrarDialogoInvitar by remember { mutableStateOf(false) }
    var mostrarDialogoAceptar by remember { mutableStateOf(false) }
    var correoColaborador by remember { mutableStateOf("") }
    var codigoParaAceptar by remember { mutableStateOf("") }
    var codigoGenerado by remember { mutableStateOf<String?>(null) }
    var enviando by remember { mutableStateOf(false) }

    LaunchedEffect(storeId, recargar) {
        cargando = true
        errorCarga = null
        try {
            miembros = withContext(Dispatchers.IO) { repository.listar(storeId) }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error listando equipo: ${e.message}", e)
            errorCarga = e.message ?: "No se pudo cargar el equipo"
        } finally {
            cargando = false
        }
    }

    fun aviso(texto: String) = Toast.makeText(context, texto, Toast.LENGTH_LONG).show()

    fun compartirCodigo(codigo: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Te invito a colaborar en mi tienda en CyberQhatu. Tu código de invitación es: $codigo"
                )
            }
            context.startActivity(Intent.createChooser(intent, "Compartir invitación"))
        } catch (e: Exception) {
            Log.e(TAG, "No se pudo abrir el menu de compartir: ${e.message}", e)
            aviso("No se pudo abrir el menú de compartir. Código: $codigo")
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

                when {
                    cargando -> Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    errorCarga != null -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("No se pudo cargar el equipo", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        Text(errorCarga.orEmpty(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Button(onClick = { recargar++ }) { Text("Reintentar") }
                    }
                    else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(miembros, key = { it.clave }) { m ->
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
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(m.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                                        Text(m.correo, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(Modifier.height(2.dp))
                                        Text(m.rol, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                    if (m.tipo != TipoMiembro.PROPIETARIO) {
                                        TextButton(onClick = {
                                            scope.launch {
                                                try {
                                                    withContext(Dispatchers.IO) {
                                                        if (m.tipo == TipoMiembro.PENDIENTE) repository.cancelarInvitacion(m.id)
                                                        else repository.eliminarMiembro(m.id)
                                                    }
                                                    recargar++
                                                } catch (e: CancellationException) {
                                                    throw e
                                                } catch (e: Exception) {
                                                    Log.e(TAG, "Error removiendo: ${e.message}", e)
                                                    aviso(e.message ?: "No se pudo remover")
                                                }
                                            }
                                        }) {
                                            Text(if (m.tipo == TipoMiembro.PENDIENTE) "Cancelar" else "Remover", color = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Invitar
            if (mostrarDialogoInvitar) {
                AlertDialog(
                    onDismissRequest = { if (!enviando) mostrarDialogoInvitar = false },
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
                                enviando = true
                                scope.launch {
                                    try {
                                        val codigo = withContext(Dispatchers.IO) {
                                            repository.invitar(storeId, correoColaborador, RolMiembro.ADMINISTRADOR.name)
                                        }
                                        correoColaborador = ""
                                        mostrarDialogoInvitar = false
                                        codigoGenerado = codigo
                                        recargar++
                                    } catch (e: CancellationException) {
                                        throw e
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Error invitando: ${e.message}", e)
                                        aviso(e.message ?: "No se pudo crear la invitación")
                                    } finally {
                                        enviando = false
                                    }
                                }
                            },
                            enabled = correoColaborador.contains("@") && !enviando
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(if (enviando) "Creando..." else "Generar código")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarDialogoInvitar = false }, enabled = !enviando) { Text("Cancelar") }
                    }
                )
            }

            // Codigo generado (se muestra siempre, aunque falle el menu de compartir)
            codigoGenerado?.let { codigo ->
                AlertDialog(
                    onDismissRequest = { codigoGenerado = null },
                    title = { Text("Código de invitación") },
                    text = { Text("$codigo\n\nCompártelo con la persona invitada. Solo funciona con el correo que ingresaste.", fontSize = 14.sp) },
                    confirmButton = {
                        Button(onClick = { compartirCodigo(codigo) }) { Text("Compartir") }
                    },
                    dismissButton = {
                        TextButton(onClick = { codigoGenerado = null }) { Text("Cerrar") }
                    }
                )
            }

            // Aceptar
            if (mostrarDialogoAceptar) {
                AlertDialog(
                    onDismissRequest = { if (!enviando) mostrarDialogoAceptar = false },
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
                                val idUsuario = SesionActual.idUsuario
                                if (idUsuario == null) {
                                    aviso("Inicia sesión para aceptar la invitación")
                                    return@Button
                                }
                                enviando = true
                                scope.launch {
                                    try {
                                        withContext(Dispatchers.IO) { repository.aceptar(codigoParaAceptar, idUsuario) }
                                        codigoParaAceptar = ""
                                        mostrarDialogoAceptar = false
                                        aviso("Invitación aceptada")
                                        onInvitacionAceptada()
                                        recargar++
                                    } catch (e: CancellationException) {
                                        throw e
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Error aceptando: ${e.message}", e)
                                        aviso(e.message ?: "No se pudo aceptar la invitación")
                                    } finally {
                                        enviando = false
                                    }
                                }
                            },
                            enabled = codigoParaAceptar.isNotBlank() && !enviando
                        ) { Text(if (enviando) "Verificando..." else "Aceptar Código") }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarDialogoAceptar = false }, enabled = !enviando) { Text("Cancelar") }
                    }
                )
            }
        }
    }
}
