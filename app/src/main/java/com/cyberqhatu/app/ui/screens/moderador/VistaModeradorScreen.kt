package com.cyberqhatu.app.ui.screens.moderador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberqhatu.app.confianza.EstadoReporte
import com.cyberqhatu.app.confianza.ModeracionStore
import com.cyberqhatu.app.ui.theme.QhatuCoral
import com.cyberqhatu.app.ui.theme.QhatuPurple

/**
 * Vista del Moderador:
 * - Bandeja de reportes de tiendas enviadas por los compradores.
 * - Sanción con strikes o desestimación del reporte.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VistaModeradorScreen(
    onCerrarSesion: () -> Unit = {}
) {
    var reporteSeleccionadoId by remember { mutableStateOf<String?>(null) }
    var textoApelacionRespuesta by remember { mutableStateOf("") }

    val reportes = ModeracionStore.reportes

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Panel de Moderación de Confianza", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = QhatuPurple) },
                actions = {
                    TextButton(onClick = onCerrarSesion) {
                        Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ReportProblem, contentDescription = null, tint = QhatuCoral)
                Spacer(Modifier.width(8.dp))
                Text("Reportes Ciudadanos de Tiendas", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = QhatuPurple)
            }

            Spacer(Modifier.height(16.dp))

            if (reportes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay reportes registrados por los compradores.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(reportes) { reporte ->
                        val strikesActuales = ModeracionStore.strikesDe(reporte.idTienda)
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(reporte.nombreTienda, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = QhatuPurple)
                                        Text("Strikes de la tienda: $strikesActuales / ${ModeracionStore.LIMITE_STRIKES}", fontSize = 12.sp, color = Color.Gray)
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = when (reporte.estado) {
                                            EstadoReporte.PENDIENTE -> Color(0xFFFFF3E0)
                                            EstadoReporte.ACEPTADO -> Color(0xFFFFEBEE)
                                            EstadoReporte.RECHAZADO -> Color(0xFFE8F5E9)
                                        }
                                    ) {
                                        Text(
                                            text = when (reporte.estado) {
                                                EstadoReporte.PENDIENTE -> "Pendiente"
                                                EstadoReporte.ACEPTADO -> "Sancionado (Strike)"
                                                EstadoReporte.RECHAZADO -> "Desestimado"
                                            },
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (reporte.estado) {
                                                EstadoReporte.PENDIENTE -> Color(0xFFE65100)
                                                EstadoReporte.ACEPTADO -> Color(0xFFC62828)
                                                EstadoReporte.RECHAZADO -> Color(0xFF2E7D32)
                                            }
                                        )
                                    }
                                }

                                Spacer(Modifier.height(8.dp))
                                Text("Motivo: ${reporte.motivo}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(reporte.descripcion, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                if (reporte.estado == EstadoReporte.PENDIENTE) {
                                    Spacer(Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { ModeracionStore.resolver(reporte.id, aceptar = false) },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Desestimar", color = Color.Gray)
                                        }

                                        Button(
                                            onClick = { ModeracionStore.resolver(reporte.id, aceptar = true) },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Aceptar (Aviso/Strike)")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
