package com.cyberqhatu.app.verificacion.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.cyberqhatu.app.data.local.database.QhatuDatabase
import com.cyberqhatu.app.ui.theme.QhatuCoral
import com.cyberqhatu.app.ui.theme.QhatuLime
import com.cyberqhatu.app.ui.theme.QhatuPurple
import com.cyberqhatu.app.data.local.TokenManager
import com.cyberqhatu.app.data.api.RetrofitClient
import kotlinx.coroutines.flow.firstOrNull
import com.cyberqhatu.app.verificacion.data.repository.VerificacionRepositoryImpl
import com.cyberqhatu.app.verificacion.domain.VerificacionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun EscanerIdentidadScreen(
    onVerificacionExitosa: (String) -> Unit,
    onVolverClick: () -> Unit,
    modifier: Modifier = Modifier,
    repository: VerificacionRepository? = null,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val activeRepository = remember(context, repository) {
        val db = QhatuDatabase.getInstance(context)
        repository ?: VerificacionRepositoryImpl(
            verificacionCacheDao = db.verificacionCacheDao(),
            verificacionDao = db.verificacionDao(),
            apiService = RetrofitClient.verificacionApiService
        )
    }

    val tokenManager = remember(context) { TokenManager(context) }
    var currentUserId by remember { mutableStateOf("USR-101") }

    LaunchedEffect(Unit) {
        val token = tokenManager.getToken().firstOrNull()
        if (token != null) {
            currentUserId = token
        }
    }

    var tienePermisoCamara by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcherPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { esOtorgado ->
        tienePermisoCamara = esOtorgado
    }

    LaunchedEffect(Unit) {
        if (!tienePermisoCamara) {
            launcherPermiso.launch(Manifest.permission.CAMERA)
        }
    }

    if (!tienePermisoCamara) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = QhatuLime,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Permiso de Cámara Requerido",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Para escanear tu carnet y verificar tu rostro, necesitamos acceso a la cámara de tu teléfono.",
                color = Color.LightGray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { launcherPermiso.launch(Manifest.permission.CAMERA) },
                colors = ButtonDefaults.buttonColors(containerColor = QhatuLime)
            ) {
                Text(
                    text = "Permitir Acceso a la Cámara",
                    color = QhatuPurple,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        return
    }

    var uiState by remember { mutableStateOf(VerificacionUiState(pasoActual = PasoVerificacion.CAPTURAR_CARNET_ANVERSO)) }

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }
    }

    LaunchedEffect(uiState.pasoActual) {
        when (uiState.pasoActual) {
            PasoVerificacion.CAPTURAR_CARNET_ANVERSO,
            PasoVerificacion.CAPTURAR_CARNET_REVERSO -> {
                cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            }
            PasoVerificacion.CAPTURAR_SELFIE -> {
                cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            }
            PasoVerificacion.PROCESANDO -> {
                val anverso = uiState.fotoCarnetAnverso
                val reverso = uiState.fotoCarnetReverso
                val selfie = uiState.fotoSelfie
                if (anverso != null && reverso != null && selfie != null) {
                    val res = withContext(Dispatchers.IO) {
                        activeRepository.verificarIdentidad(currentUserId, anverso, reverso, selfie)
                    }
                    uiState = uiState.copy(
                        pasoActual = PasoVerificacion.RESULTADO,
                        resultado = res,
                        estaCargando = false
                    )
                }
            }
            else -> {}
        }
    }

    DisposableEffect(lifecycleOwner) {
        cameraController.bindToLifecycle(lifecycleOwner)
        onDispose {
            cameraController.unbind()
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        if (uiState.pasoActual == PasoVerificacion.CAPTURAR_CARNET_ANVERSO ||
            uiState.pasoActual == PasoVerificacion.CAPTURAR_CARNET_REVERSO ||
            uiState.pasoActual == PasoVerificacion.CAPTURAR_SELFIE) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        controller = cameraController
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Marco Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.pasoActual == PasoVerificacion.CAPTURAR_CARNET_ANVERSO ||
                    uiState.pasoActual == PasoVerificacion.CAPTURAR_CARNET_REVERSO) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .border(3.dp, QhatuLime, RoundedCornerShape(16.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .border(3.dp, QhatuLime, CircleShape)
                    )
                }
            }

            // Barra inferior con botón de captura
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when (uiState.pasoActual) {
                        PasoVerificacion.CAPTURAR_CARNET_ANVERSO -> "Paso 1/3: Encuadra el ANVERSO de tu C.I. (Frente)"
                        PasoVerificacion.CAPTURAR_CARNET_REVERSO -> "Paso 2/3: Encuadra el REVERSO de tu C.I. (Atrás)"
                        else -> "Paso 3/3: Ubica tu rostro dentro del círculo para la selfie"
                    },
                    color = Color.White,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                IconButton(
                    onClick = {
                        val mainExecutor = ContextCompat.getMainExecutor(context)
                        cameraController.takePicture(
                            mainExecutor,
                            object : ImageCapture.OnImageCapturedCallback() {
                                override fun onCaptureSuccess(image: ImageProxy) {
                                    val bitmap = imageProxyToBitmap(image)
                                    image.close()

                                    if (uiState.pasoActual == PasoVerificacion.CAPTURAR_CARNET_ANVERSO) {
                                        uiState = uiState.copy(
                                            fotoCarnetAnverso = bitmap,
                                            pasoActual = PasoVerificacion.CAPTURAR_CARNET_REVERSO
                                        )
                                    } else if (uiState.pasoActual == PasoVerificacion.CAPTURAR_CARNET_REVERSO) {
                                        uiState = uiState.copy(
                                            fotoCarnetReverso = bitmap,
                                            pasoActual = PasoVerificacion.CAPTURAR_SELFIE
                                        )
                                    } else {
                                        uiState = uiState.copy(
                                            fotoSelfie = bitmap,
                                            pasoActual = PasoVerificacion.PROCESANDO,
                                            estaCargando = true
                                        )
                                    }
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    Log.e("EscanerIdentidad", "Error al capturar imagen", exception)
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .size(72.dp)
                        .background(QhatuLime, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Tomar Foto",
                        tint = QhatuPurple,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        } else if (uiState.pasoActual == PasoVerificacion.PROCESANDO) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = QhatuLime, modifier = Modifier.size(56.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Procesando prueba biométrica...",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Comparando rostro del carnet con la selfie en vivo",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        } else if (uiState.pasoActual == PasoVerificacion.RESULTADO) {
            val resultado = uiState.resultado
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (resultado?.esExitoso == true) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Éxito",
                        tint = QhatuLime,
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "¡Identidad Verificada!",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Similitud biométrica: ${"%.1f".format(resultado.porcentajeSimilitud)}%\nC.I. Detectado: ${resultado.numeroCI ?: "Verificado"}",
                        color = Color.LightGray,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { onVerificacionExitosa(resultado.numeroCI ?: "") },
                        colors = ButtonDefaults.buttonColors(containerColor = QhatuLime),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Continuar", color = QhatuPurple, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = QhatuCoral,
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "No se pudo verificar la identidad",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = resultado?.mensajeError ?: "Los rostros no coinciden con suficiente claridad. Intenta con mejor iluminación.",
                        color = Color.LightGray,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            uiState = VerificacionUiState(pasoActual = PasoVerificacion.CAPTURAR_CARNET_ANVERSO)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = QhatuLime),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Reintentar Escaneo", color = QhatuPurple, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}
