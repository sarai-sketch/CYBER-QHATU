package com.cyberqhatu.app.ui.screens.comprador

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyberqhatu.app.ui.theme.QhatuPurple
import kotlinx.coroutines.launch

/**
 * Pantalla de Inicio de Sesión con dos pestañas: "Ingresar" y "Registrarse".
 * onLoginSuccess recibe el correo con el que se ingresó o registró.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginCompradorScreen(
    onLoginSuccess: (email: String) -> Unit = {},
    onBackClick: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.provideFactory())
) {
    var tab by remember { mutableIntStateOf(0) } // 0 = Ingresar, 1 = Registrarse
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var verPassword by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                if (state.isLogin) {
                    onLoginSuccess(state.email)
                    authViewModel.resetState()
                } else {
                    launch { snackbarHostState.showSnackbar(state.message) }
                    tab = 0
                    authViewModel.resetState()
                }
            }
            is AuthState.Error -> {
                launch { snackbarHostState.showSnackbar(state.message) }
                authViewModel.resetState()
            }
            else -> {}
        }
    }

    val emailValido = android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val coinciden = password == confirmar
    val puedeIngresar = emailValido && password.isNotBlank()
    val puedeRegistrar = emailValido && password.length >= 6 && coinciden && nombre.isNotBlank()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Sesión", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = QhatuPurple) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
        ) {
            TabRow(selectedTabIndex = tab) {
                Tab(
                    selected = tab == 0,
                    onClick = { tab = 0; password = ""; confirmar = "" },
                    text = { Text("Ingresar") }
                )
                Tab(
                    selected = tab == 1,
                    onClick = { tab = 1; password = ""; confirmar = "" },
                    text = { Text("Registrarse") }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (tab == 0) "Bienvenido a Cyber-Qhatu" else "Crea tu cuenta",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = QhatuPurple
                )
                Text(
                    text = if (tab == 0) "Accede para guardar favoritos y conectar con tiendas."
                    else "Regístrate con tu correo para empezar.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (tab == 1) {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre completo") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = email.isNotEmpty() && !emailValido,
                    supportingText = { if (email.isNotEmpty() && !emailValido) Text("Correo no válido") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = if (verPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { verPassword = !verPassword }) {
                            Icon(
                                imageVector = if (verPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Mostrar u ocultar contraseña"
                            )
                        }
                    },
                    isError = tab == 1 && password.isNotEmpty() && password.length < 6,
                    supportingText = { if (tab == 1) Text("Mínimo 6 caracteres") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (tab == 1) {
                    OutlinedTextField(
                        value = confirmar,
                        onValueChange = { confirmar = it },
                        label = { Text("Confirmar contraseña") },
                        singleLine = true,
                        visualTransformation = if (verPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = confirmar.isNotEmpty() && !coinciden,
                        supportingText = { if (confirmar.isNotEmpty() && !coinciden) Text("Las contraseñas no coinciden") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Button(
                    onClick = {
                        if (tab == 0) {
                            authViewModel.login(email.trim(), password)
                        } else {
                            authViewModel.register(nombre.trim(), email.trim(), password)
                        }
                    },
                    enabled = (if (tab == 0) puedeIngresar else puedeRegistrar) && authState !is AuthState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = if (tab == 0) "Ingresar" else "Registrarse",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
