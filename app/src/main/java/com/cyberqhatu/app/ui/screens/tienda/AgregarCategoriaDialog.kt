package com.cyberqhatu.app.ui.screens.tienda

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AgregarCategoriaDialog(
    onDismissRequest: () -> Unit,
    viewModel: CategoriaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.exito) {
        if (uiState.exito) {
            onDismissRequest()
            viewModel.resetEstado()
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Agregar Nueva Categoría") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.nombreNuevaCategoria,
                    onValueChange = { viewModel.onNombreChange(it) },
                    label = { Text("Nombre de la categoría") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState.error != null) {
                    Text(
                        text = uiState.error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (uiState.guardando) {
                    Spacer(Modifier.height(16.dp))
                    CircularProgressIndicator()
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { viewModel.guardarCategoria() },
                enabled = uiState.nombreNuevaCategoria.isNotBlank() && !uiState.guardando
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}
