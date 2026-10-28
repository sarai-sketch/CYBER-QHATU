package com.cyberqhatu.app.ui.screens.comprador

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberqhatu.app.ui.theme.QhatuPurple

/**
 * Pantalla dedicada para que los compradores logueados dejen una reseña y calificación (1 a 5 estrellas).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EscribirResenaScreen(
    nombreItem: String = "Puesto Don Carlos",
    onPublicarResena: (rating: Int, comentario: String) -> Unit = { _, _ -> },
    onBackClick: () -> Unit = {}
) {
    var rating by remember { mutableIntStateOf(5) }
    var comentario by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escribir Reseña", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = QhatuPurple) },
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
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text("Calificar: $nombreItem", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = QhatuPurple)
                Text("Selecciona una calificación de 1 a 5 estrellas y deja tu opinión para la comunidad.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                // Selector de 5 estrellas interactivo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= rating) Icons.Default.Star else Icons.Outlined.Star,
                            contentDescription = "$i estrellas",
                            tint = if (i <= rating) Color(0xFFFFC107) else Color.Gray,
                            modifier = Modifier
                                .size(44.dp)
                                .clickable { rating = i }
                                .padding(4.dp)
                        )
                    }
                }

                Text(
                    text = "$rating de 5 estrellas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = QhatuPurple,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                OutlinedTextField(
                    value = comentario,
                    onValueChange = { comentario = it },
                    label = { Text("Escribe tu comentario / opinión *") },
                    placeholder = { Text("Ej. Excelente atención, producto de calidad y entrega rápida.") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = {
                        if (comentario.isNotBlank()) {
                            onPublicarResena(rating, comentario.trim())
                        }
                    },
                    enabled = comentario.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Publicar Reseña", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
