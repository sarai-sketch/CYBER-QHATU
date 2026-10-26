package com.cyberqhatu.app.ui.screens.roles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberqhatu.app.ui.theme.QhatuCoral
import com.cyberqhatu.app.ui.theme.QhatuPurple

data class ReporteItem(
    val id: String,
    val tienda: String,
    val motivo: String,
    val descripcion: String,
    var estado: String // "Pendiente", "Aprobado (Sancionado)", "Desestimado"
)

/**
 * Vista del Moderador:
 * - Bandeja de reportes de tiendas y productos enviados por los compradores.
 * - Validación de veracidad y toma de decisiones (sancionar tienda o desestimar).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VistaModeradorScreen(
    onCerrarSesion: () -> Unit = {}
) {
    var reportes by remember {
        mutableStateOf(
            listOf(
                ReporteItem("R-001", "Moda Andina", "No entregó el producto", "Compré un aguayo hace 5 días y no responden los mensajes.", "Pendiente"),
                ReporteItem("R-002", "Artesanías El Alto", "Producto distinto al anunciado", "El tamaño del ekeko recibido es la mitad del publicado.", "Pendiente"),
                ReporteItem("R-003", "TecnoBolivia", "Trato irrespetuoso", "El vendedor fue grosero cuando reclamé por garantía.", "Pendiente")
            )
        )
    }

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
                Text("Reportes Pendientes de Revisión", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = QhatuPurple)
            }

            Spacer(Modifier.height(16.dp))

            if (reportes.all { it.estado != "Pendiente" }) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay reportes pendientes por revisar.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(reportes) { reporte ->
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
                                    Text(reporte.tienda, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = QhatuPurple)
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = when (reporte.estado) {
                                            "Pendiente" -> Color(0xFFFFF3E0)
                                            "Sancionado" -> Color(0xFFFFEBEE)
                                            else -> Color(0xFFE8F5E9)
                                        }
                                    ) {
                                        Text(
                                            text = reporte.estado,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (reporte.estado) {
                                                "Pendiente" -> Color(0xFFE65100)
                                                "Sancionado" -> Color(0xFFC62828)
                                                else -> Color(0xFF2E7D32)
                                            }
                                        )
                                    }
                                }

                                Spacer(Modifier.height(8.dp))
                                Text("Motivo: ${reporte.motivo}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(reporte.descripcion, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                if (reporte.estado == "Pendiente") {
                                    Spacer(Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                reportes = reportes.map { if (it.id == reporte.id) it.copy(estado = "Desestimado") else it }
                                            },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Desestimar", color = Color.Gray)
                                        }

                                        Button(
                                            onClick = {
                                                reportes = reportes.map { if (it.id == reporte.id) it.copy(estado = "Sancionado") else it }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Sancionar Tienda")
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
