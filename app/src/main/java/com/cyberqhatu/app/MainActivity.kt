package com.cyberqhatu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.cyberqhatu.app.data.local.database.QhatuDatabase
import com.cyberqhatu.app.ui.screens.tienda.MarketplaceAppEntry
import com.cyberqhatu.app.ui.theme.CyberQhatuTheme
import com.cyberqhatu.app.verificacion.data.repository.VerificacionRepositoryImpl
import com.cyberqhatu.app.verificacion.ui.EscanerIdentidadScreen
import com.cyberqhatu.app.verificacion.ui.InstruccionesVerificacionScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CyberQhatuTheme {
                MainNavigationApp()
            }
        }
    }
}

@Composable
fun MainNavigationApp() {
    val context = LocalContext.current
    val repository = remember(context) {
        val db = QhatuDatabase.getInstance(context)
        VerificacionRepositoryImpl(
            verificacionCacheDao = db.verificacionCacheDao(),
            verificacionDao = db.verificacionDao(),
        )
    }

    val estadoLocalFlow = remember(repository) {
        repository.obtenerEstadoVerificacionLocal("USR-101")
    }
    val estadoLocal by estadoLocalFlow.collectAsState(initial = null)
    val estaVerificado = estadoLocal?.esExitoso == true

    var modoVerificacionStep by remember { mutableIntStateOf(0) }

    when (modoVerificacionStep) {
        1 -> {
            InstruccionesVerificacionScreen(
                onComenzarClick = { modoVerificacionStep = 2 },
                onVolverClick = { modoVerificacionStep = 0 },
            )
        }
        2 -> {
            EscanerIdentidadScreen(
                onVerificacionExitosa = { _: String ->
                    modoVerificacionStep = 0
                },
                onVolverClick = { modoVerificacionStep = 0 },
            )
        }
        else -> {
            MarketplaceAppEntry(
                estaVerificado = estaVerificado,
                onVerificarIdentidadClick = { modoVerificacionStep = 1 },
            )
        }
    }
}
