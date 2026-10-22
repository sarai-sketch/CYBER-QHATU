package com.cyberqhatu.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberqhatu.app.ui.theme.CyberQhatuTheme
import com.cyberqhatu.app.data.local.TokenManager
import com.cyberqhatu.app.data.local.database.QhatuDatabase
import com.cyberqhatu.app.data.model.LoginRequest
import com.cyberqhatu.app.data.api.RetrofitClient
import com.cyberqhatu.app.data.repository.AuthRepository
import com.cyberqhatu.app.ui.viewmodel.AuthState
import com.cyberqhatu.app.ui.viewmodel.AuthViewModel
import com.cyberqhatu.app.ui.viewmodel.AuthViewModelFactory

import com.cyberqhatu.app.data.model.RegisterRequest
import com.cyberqhatu.app.ui.auth.PantallaConfirmacionOTP

@Composable
fun PantallaBienvenida(
    onContinuarClicked: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    // Using Retrofit for API verification
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(AuthRepository(RetrofitClient.getAuthApiService(context), QhatuDatabase.getInstance(context).authCacheDao(), tokenManager))
    )

    val authState by viewModel.authState.collectAsState()

    var isLoginMode by remember { mutableStateOf(true) }
    var showOtpScreen by remember { mutableStateOf(false) }
    var registeredEmail by remember { mutableStateOf("") }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    if (showOtpScreen) {
        PantallaConfirmacionOTP(
            numeroTelefono = registeredEmail, // Using email instead of phone
            onConfirmarClicked = { code ->
                viewModel.verifyEmail(com.cyberqhatu.app.data.model.VerifyEmailRequest(registeredEmail, code))
            },
            onReenviarClicked = {
                viewModel.resendCode(com.cyberqhatu.app.data.model.ResendCodeRequest(registeredEmail))
            },
            authState = authState,
            onSuccess = {
                isLoginMode = true
                showOtpScreen = false
                viewModel.resetState()
            }
        )
        return
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Seccion del Logo e Identidad Visual
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Logo CyberQhatu",
                        modifier = Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "CYBER-QHATU",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "Tu comercio local confiable",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Seccion de Formulario de Ingreso
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isLoginMode) "¡Bienvenido!" else "Crear Cuenta",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isLoginMode) "Inicia sesión con tu correo electrónico para continuar" else "Regístrate para continuar",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (!isLoginMode) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nombre Completo") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo Electrónico") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email"
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (isLoginMode) {
                                viewModel.login(LoginRequest(email, password))
                            } else {
                                viewModel.register(RegisterRequest(email, password, name))
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = authState !is AuthState.Loading && email.isNotBlank() && password.isNotBlank() && (isLoginMode || name.isNotBlank())
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                text = if (isLoginMode) "Iniciar Sesión" else "Registrarse",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = {
                        isLoginMode = !isLoginMode
                        viewModel.resetState()
                    }) {
                        Text(if (isLoginMode) "¿No tienes cuenta? Regístrate" else "¿Ya tienes cuenta? Inicia sesión")
                    }

                    when (val state = authState) {
                        is AuthState.Success -> {
                            Text("¡Inicio de sesión exitoso!", color = Color(0xFF4CAF50))
                            LaunchedEffect(Unit) {
                                onContinuarClicked(email)
                            }
                        }
                        is AuthState.RegisterSuccess -> {
                            Text("Registro exitoso.", color = Color(0xFF4CAF50))
                            LaunchedEffect(Unit) {
                                registeredEmail = state.email
                                showOtpScreen = true
                            }
                        }
                        is AuthState.Error -> {
                            Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                        }
                        else -> {}
                    }
                }
            }

            // Pie de página
            Text(
                text = "Al continuar, aceptas nuestros Términos y Condiciones",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

