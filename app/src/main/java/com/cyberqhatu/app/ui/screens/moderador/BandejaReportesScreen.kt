package com.cyberqhatu.app.ui.screens.moderador

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberqhatu.app.ui.theme.QhatuCoral
import com.cyberqhatu.app.ui.theme.QhatuPurple

data class ReporteModel(
    val id: String,
    val tienda: String,
    val motivo: String,
    val descripcion: String,
    val estado: String
)

/**
 * Pantalla dedicada a la Bandeja de Reportes para el Moderador.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandejaReportesScreen(
    reportes: List<ReporteModel>,
    onReporteClick: (ReporteModel) -> Unit,
    onCerrarSesion: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bandeja de Reportes (Moderador)", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = QhatuPurple) },
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
                    Text("No hay reportes registrados.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(reportes) { reporte ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onReporteClick(reporte) }
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
                                Text(reporte.descripcion, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                            }
                        }
                    }
                }
            }
        }
    }
}
