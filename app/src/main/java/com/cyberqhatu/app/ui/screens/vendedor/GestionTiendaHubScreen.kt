package com.cyberqhatu.app.ui.screens.vendedor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.cyberqhatu.app.confianza.EstadoApelacion
import com.cyberqhatu.app.confianza.EstadoReporte
import com.cyberqhatu.app.confianza.ModeracionStore
import com.cyberqhatu.app.confianza.ReporteTienda
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberqhatu.app.ui.theme.QhatuPurple

/**
 * Panel de Gestión de la Tienda creada (Hub):
 * - Ver perfil de la tienda
 * - Agregar producto
 * - Inventario
 * - Administradores / Colaboradores
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionTiendaHubScreen(
    idTienda: String,
    nombreTienda: String = "Mi tienda",
    onVerPerfilTienda: () -> Unit = {},
    onAgregarProducto: () -> Unit = {},
    onVerInventario: () -> Unit = {},
    onGestionarColaboradores: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Mi Tienda", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = QhatuPurple) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val strikes = ModeracionStore.strikesDe(idTienda)
                val suspendida = ModeracionStore.estaSuspendida(idTienda)
                val misReportes = ModeracionStore.reportesDe(idTienda)
                var reporteApelando by remember { mutableStateOf<ReporteTienda?>(null) }
                var textoApelacion by remember { mutableStateOf("") }

                // Al abrir el panel, la tienda ya "vio" los avisos (se quita la insignia del inicio)
                LaunchedEffect(Unit) { ModeracionStore.marcarVistos(idTienda) }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Store, contentDescription = null, modifier = Modifier.size(40.dp), tint = QhatuPurple)
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(nombreTienda, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = QhatuPurple)
                            Text("Administra tus productos, inventario y equipo de trabajo.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }

                // Estado de strikes de la tienda
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (strikes > 0) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "Strikes: $strikes de ${ModeracionStore.LIMITE_STRIKES}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            when {
                                suspendida -> "Tu tienda está suspendida por acumular strikes."
                                strikes > 0 -> "Tienes strikes activos. Si crees que es un error, puedes apelar."
                                else -> "Tu tienda no tiene sanciones."
                            },
                            fontSize = 12.sp
                        )
                    }
                }

                // Reportes recibidos (aviso a la tienda) con opción de apelar
                if (misReportes.isNotEmpty()) {
                    Text("Reportes recibidos", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = QhatuPurple)
                    misReportes.forEach { r ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("${r.id} · ${r.motivo}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    r.descripcion,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    when {
                                        r.estado == EstadoReporte.PENDIENTE -> "En revisión por el moderador."
                                        r.estado == EstadoReporte.RECHAZADO -> "Reporte rechazado: sin sanción."
                                        r.estadoApelacion == EstadoApelacion.PENDIENTE -> "Strike aplicado. Tu apelación está en revisión."
                                        r.estadoApelacion == EstadoApelacion.ACEPTADA -> "Apelación aceptada: se retiró el strike."
                                        r.estadoApelacion == EstadoApelacion.RECHAZADA -> "Apelación rechazada: el strike se mantiene."
                                        else -> "Reporte aceptado: se aplicó un strike a tu tienda."
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (r.estado == EstadoReporte.ACEPTADO && r.estadoApelacion == EstadoApelacion.NINGUNA) {
                                    OutlinedButton(onClick = { reporteApelando = r; textoApelacion = "" }) {
                                        Text("Apelar")
                                    }
                                }
                            }
                        }
                    }
                }

                reporteApelando?.let { r ->
                    AlertDialog(
                        onDismissRequest = { reporteApelando = null },
                        title = { Text("Apelar ${r.id}") },
                        text = {
                            OutlinedTextField(
                                value = textoApelacion,
                                onValueChange = { textoApelacion = it },
                                label = { Text("Explica por qué el reporte es incorrecto") },
                                minLines = 3,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        confirmButton = {
                            TextButton(
                                enabled = textoApelacion.isNotBlank(),
                                onClick = {
                                    ModeracionStore.apelar(r.id, textoApelacion.trim())
                                    reporteApelando = null
                                }
                            ) { Text("Enviar apelación") }
                        },
                        dismissButton = {
                            TextButton(onClick = { reporteApelando = null }) { Text("Cancelar") }
                        }
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Opciones del Hub
                BotonOpcionHub(
                    titulo = "Ver Perfil de la Tienda",
                    descripcion = "Visualiza cómo ven los compradores tu puesto",
                    icon = Icons.Default.Store,
                    onClick = onVerPerfilTienda
                )

                BotonOpcionHub(
                    titulo = "Agregar Producto",
                    descripcion = "Publica un nuevo artículo al catálogo",
                    icon = Icons.Default.AddBox,
                    onClick = onAgregarProducto
                )

                BotonOpcionHub(
                    titulo = "Inventario y Ventas",
                    descripcion = "Controla stock, precios y registro de ventas",
                    icon = Icons.Default.Inventory2,
                    onClick = onVerInventario
                )

                BotonOpcionHub(
                    titulo = "Administradores de la Tienda",
                    descripcion = "Agrega o remueve colaboradores con cuenta activa",
                    icon = Icons.Default.Group,
                    onClick = onGestionarColaboradores
                )
            }
        }
    }
}

@Composable
fun BotonOpcionHub(
    titulo: String,
    descripcion: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = QhatuPurple.copy(alpha = 0.1f)
            ) {
                Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = QhatuPurple)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = QhatuPurple)
                Spacer(Modifier.height(2.dp))
                Text(descripcion, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}