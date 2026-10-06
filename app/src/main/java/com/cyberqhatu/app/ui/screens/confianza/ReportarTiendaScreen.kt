package com.cyberqhatu.app.ui.screens.confianza

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberqhatu.app.ui.theme.*

private val motivos = listOf(
    "No entregó el producto",
    "Producto distinto al anunciado",
    "Trato irrespetuoso"
)

@Composable
fun ReportarTiendaScreen(nombreTienda: String = "Moda Andina") {
    var motivoSeleccionado by remember { mutableStateOf(motivos.first()) }
    var descripcion by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Banner Superior Encabezado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                .background(QhatuPurple)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Text(
                    text = "Confianza y Seguridad",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Reportar Tienda",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Tarjeta de la tienda
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(nombreTienda, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = QhatuPurple)
                    Spacer(Modifier.height(4.dp))
                    Text("Stock 2 · Calle Comercio 123", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Motivo del reporte
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Motivo del reporte *", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = QhatuPurple)

                motivos.forEach { motivo ->
                    val seleccionado = motivo == motivoSeleccionado
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (seleccionado) QhatuPurpleLight else Color.White)
                            .border(
                                width = if (seleccionado) 1.5.dp else 1.dp,
                                color = if (seleccionado) QhatuPurple else QhatuBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { motivoSeleccionado = motivo }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = seleccionado,
                            onClick = { motivoSeleccionado = motivo },
                            colors = RadioButtonDefaults.colors(selectedColor = QhatuPurple)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(motivo, fontSize = 13.sp, fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Normal)
                    }
                }
            }

            // Descripción
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Describe lo sucedido *", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = QhatuPurple)

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    placeholder = { Text("Ej: El vendedor no entregó el pedido a tiempo...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = QhatuPurple,
                        focusedLabelColor = QhatuPurple
                    )
                )
            }

            // Evidencia
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Evidencia (Fotos / Capturas)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = QhatuPurple)

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (index == 0) QhatuPurpleLight else Color.White)
                                .border(
                                    width = 1.5.dp,
                                    color = if (index == 0) QhatuPurple else QhatuBorder,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (index == 0) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Adjuntar evidencia",
                                    tint = QhatuPurple,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Botón enviar
            Button(
                onClick = { /* .*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = QhatuCoral)
            ) {
                Text("Enviar Reporte", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Text(
                "La tienda puede refutar con su propia evidencia. Un moderador revisará y tomará una decisión.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun ReportarTiendaScreenPreview() {
    ReportarTiendaScreen()
}
