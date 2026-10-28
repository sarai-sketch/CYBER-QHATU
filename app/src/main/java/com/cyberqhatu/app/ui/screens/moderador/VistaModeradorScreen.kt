package com.cyberqhatu.app.ui.screens.moderador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cyberqhatu.app.confianza.EstadoReporte
import com.cyberqhatu.app.confianza.ModeracionStore
import com.cyberqhatu.app.ui.screens.tienda.DomainMockData
import com.cyberqhatu.app.ui.theme.QhatuCoral
import com.cyberqhatu.app.ui.theme.QhatuLime
import com.cyberqhatu.app.ui.theme.QhatuPurple
import com.cyberqhatu.app.ui.theme.QhatuPurpleLight

/**
 * Vista Completa del Moderador:
 * - Pestaña 1: Bandeja de Reportes de compradores con imágenes de evidencia adjuntas.
 * - Pestaña 2: Inspección de Tiendas y Productos para corroborar reputación y reseñas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VistaModeradorScreen(
    onCerrarSesion: () -> Unit = {}
) {
    var tabSeleccionada by remember { mutableIntStateOf(0) }
    val reportes = ModeracionStore.reportes
    val tiendas = DomainMockData.tiendas
    val productos = DomainMockData.productos

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Panel de Moderación y Confianza", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = QhatuPurple) },
                actions = {
                    TextButton(onClick = onCerrarSesion) {
                        Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
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
        ) {
            TabRow(
                selectedTabIndex = tabSeleccionada,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = QhatuPurple
            ) {
                Tab(
                    selected = tabSeleccionada == 0,
                    onClick = { tabSeleccionada = 0 },
                    text = { Text("Reportes y Evidencias (${reportes.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = tabSeleccionada == 1,
                    onClick = { tabSeleccionada = 1 },
                    text = { Text("Tiendas y Productos", fontWeight = FontWeight.Bold) }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (tabSeleccionada == 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ReportProblem, contentDescription = null, tint = QhatuCoral)
                        Spacer(Modifier.width(8.dp))
                        Text("Bandeja de Reportes con Capturas de Evidencia", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = QhatuPurple)
                    }

                    Spacer(Modifier.height(14.dp))

                    if (reportes.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay reportes pendientes registrados.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(reportes) { reporte ->
                                val strikesActuales = ModeracionStore.strikesDe(reporte.idTienda)
                                Card(
                                    shape = RoundedCornerShape(16.dp),
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
                                                shape = RoundedCornerShape(8.dp),
                                                color = when (reporte.estado) {
                                                    EstadoReporte.PENDIENTE -> Color(0xFFFFF3E0)
                                                    EstadoReporte.ACEPTADO -> Color(0xFFFFEBEE)
                                                    EstadoReporte.RECHAZADO -> Color(0xFFE8F5E9)
                                                }
                                            ) {
                                                Text(
                                                    text = when (reporte.estado) {
                                                        EstadoReporte.PENDIENTE -> "Pendiente"
                                                        EstadoReporte.ACEPTADO -> "Sancionado"
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
                                        Text("Motivo: ${reporte.motivo}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                        Spacer(Modifier.height(4.dp))
                                        Text(reporte.descripcion, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                        Spacer(Modifier.height(10.dp))
                                        Text("Evidencias adjuntas por el comprador:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = QhatuPurple)
                                        Spacer(Modifier.height(6.dp))

                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            item {
                                                Box(
                                                    modifier = Modifier
                                                        .size(70.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(QhatuPurpleLight),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = QhatuPurple)
                                                }
                                            }
                                        }

                                        if (reporte.estado == EstadoReporte.PENDIENTE) {
                                            Spacer(Modifier.height(14.dp))
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
                                                    colors = ButtonDefaults.buttonColors(containerColor = QhatuCoral),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text("Aplicar Strike", fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Store, contentDescription = null, tint = QhatuPurple)
                        Spacer(Modifier.width(8.dp))
                        Text("Inspección de Tiendas y Productos Registrados", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = QhatuPurple)
                    }

                    Spacer(Modifier.height(14.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item {
                            Text("Tiendas en la Feria (${tiendas.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = QhatuPurple)
                        }

                        items(tiendas) { tienda ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(QhatuPurpleLight),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(tienda.nombre.take(2).uppercase(), fontWeight = FontWeight.Bold, color = QhatuPurple)
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(tienda.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Estado: ${tienda.estado.name} · Strikes: ${tienda.strikes}", fontSize = 12.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(Modifier.height(8.dp))
                            Text("Catálogo de Productos para Inspección (${productos.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = QhatuPurple)
                        }

                        items(productos) { prod ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = QhatuPurple, modifier = Modifier.size(24.dp))
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(prod.nombre, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Bs ${prod.precio.toInt()} · Stock: ${prod.stock}", fontSize = 12.sp, color = Color.Gray)
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
