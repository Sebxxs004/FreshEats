package com.fresheats.app.ui.screens.login

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import android.widget.Toast
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fresheats.app.R
import com.fresheats.app.ui.theme.FreshEatsTheme
import com.fresheats.app.ui.theme.GreenBright
import com.fresheats.app.ui.theme.GreenDark
import com.fresheats.app.ui.theme.GreenLight
import com.fresheats.app.ui.theme.GreenPrimary
import com.fresheats.app.ui.theme.GreenSurface
import com.fresheats.app.ui.theme.GrayMid
import com.fresheats.app.ui.theme.OrangeBright
import com.fresheats.app.ui.theme.OrangePrimary
import com.fresheats.app.ui.theme.White

// ─────────────────────────────────────────────────────────────────────────────
// FreshEats — LoginScreen
// Imágenes reales integradas:
//   • bg_login_hero.jpg   → fondo de la sección hero (con gradiente encima)
//   • ic_fresheats_logo.png → logo circular sobre el título
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun LoginScreen(
    onLoginSuccess:    () -> Unit,
    onNavigateRegister: () -> Unit,
    viewModel:         AuthViewModel = viewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // ── Manejo del estado AuthState ────────────────────────
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> onLoginSuccess()
            is AuthState.Error -> {
                val errorMsg = (authState as AuthState.Error).message
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
            else -> {}
        }
    }

    val isLoading = authState is AuthState.Loading

    // ── Estado del formulario ──────────────────────────────────────────────
    var email           by rememberSaveable { mutableStateOf("") }
    var password        by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // ── Animación de entrada (fade + slide) ────────────────────────────────
    var formVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { formVisible = true }
    val formAlpha by animateFloatAsState(
        targetValue = if (formVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 700, easing = EaseOutCubic),
        label = "formAlpha"
    )

    // ── Gradiente de fondo principal ───────────────────────────────────────
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            GreenDark,           // Verde bosque oscuro en la parte superior
            GreenPrimary,        // Verde medio
            GreenBright,         // Verde brillante en la transición
            GreenSurface         // Verde muy claro → empalma con la card blanca
        ),
        startY = 0f,
        endY = 1200f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
    ) {

        // ── Imagen de fondo hero (detrás del gradiente verde) ───────────────
        Image(
            painter            = painterResource(id = R.drawable.bg_login_hero),
            contentDescription = null,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier
                .fillMaxWidth()
                .height(460.dp)
                .align(Alignment.TopCenter)
                .alpha(0.22f)   // Transparencia: el gradiente verde domina visualmente
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Espaciado superior ─────────────────────────────────────────
            Spacer(modifier = Modifier.height(48.dp))

            // ──────────────────────────────────────────────────────────────
            // SECCIÓN HERO — Logo + Título
            // ──────────────────────────────────────────────────────────────

            // ── Logo circular FreshEats ───────────────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(118.dp)
                    .shadow(
                        elevation    = 20.dp,
                        shape        = CircleShape,
                        ambientColor = GreenDark.copy(alpha = 0.5f),
                        spotColor    = GreenDark.copy(alpha = 0.5f)
                    )
                    .clip(CircleShape)
                    .background(White)
            ) {
                Image(
                    painter            = painterResource(id = R.drawable.ic_fresheats_logo),
                    contentDescription = "Logo FreshEats",
                    contentScale       = ContentScale.Fit,
                    modifier           = Modifier
                        .size(104.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Título principal ───────────────────────────────────────────
            Text(
                text       = "Fresh Eats",
                style      = MaterialTheme.typography.displaySmall.copy(
                    fontWeight    = FontWeight.ExtraBold,
                    fontSize      = 42.sp,
                    letterSpacing = (-1).sp
                ),
                color      = White,
                textAlign  = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ── Subtítulo / tagline ────────────────────────────────────────
            Text(
                text      = "Comida fresca, vida saludable 🥗",
                style     = MaterialTheme.typography.bodyLarge,
                color     = White.copy(alpha = 0.80f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ──────────────────────────────────────────────────────────────
            // TARJETA DE FORMULARIO (glassmorphism suave)
            // ──────────────────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(formAlpha)
                    .padding(horizontal = 20.dp),
                shape  = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomStart = 28.dp, bottomEnd = 28.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 36.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {

                    // ── Encabezado del formulario ──────────────────────────
                    Text(
                        text  = "Iniciar Sesión",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = GreenDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text  = "Bienvenido de vuelta",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrayMid
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // ── Campo: Correo electrónico ──────────────────────────
                    OutlinedTextField(
                        value         = email,
                        onValueChange = { email = it },
                        label         = { Text("Correo electrónico") },
                        placeholder   = { Text("tucorreo@ejemplo.com", color = GrayMid.copy(alpha = 0.6f)) },
                        leadingIcon   = {
                            Icon(
                                imageVector        = Icons.Default.Email,
                                contentDescription = "Ícono correo",
                                tint               = GreenPrimary
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction    = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        singleLine    = true,
                        shape         = RoundedCornerShape(14.dp),
                        colors        = loginTextFieldColors(),
                        modifier      = Modifier.fillMaxWidth(),
                        enabled       = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Campo: Contraseña ──────────────────────────────────
                    OutlinedTextField(
                        value         = password,
                        onValueChange = { password = it },
                        label         = { Text("Contraseña") },
                        placeholder   = { Text("••••••••", color = GrayMid.copy(alpha = 0.6f)) },
                        leadingIcon   = {
                            Icon(
                                imageVector        = Icons.Default.Lock,
                                contentDescription = "Ícono candado",
                                tint               = GreenPrimary
                            )
                        },
                        trailingIcon  = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Default.Visibility
                                    else
                                        Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible)
                                        "Ocultar contraseña"
                                    else
                                        "Mostrar contraseña",
                                    tint = GrayMid
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction    = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                if(email.isNotBlank() && password.isNotBlank()) {
                                    viewModel.login(email, password)
                                }
                            }
                        ),
                        singleLine    = true,
                        shape         = RoundedCornerShape(14.dp),
                        colors        = loginTextFieldColors(),
                        modifier      = Modifier.fillMaxWidth(),
                        enabled       = !isLoading
                    )

                    // ── "¿Olvidaste tu contraseña?" ────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { /* TODO: Navegar a RecoverPasswordScreen */ }
                        ) {
                            Text(
                                text  = "¿Olvidaste tu contraseña?",
                                color = OrangePrimary,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // ── Botón principal: INICIAR SESIÓN ────────────────────
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            if(email.isNotBlank() && password.isNotBlank()) {
                                viewModel.login(email, password)
                            } else {
                                Toast.makeText(context, "Llena todos los campos", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape  = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenPrimary,
                            contentColor   = White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation  = 6.dp,
                            pressedElevation  = 2.dp
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = White,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text  = "Iniciar Sesión",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize   = 17.sp
                                )
                            )
                        }
                    }



                    Spacer(modifier = Modifier.height(28.dp))

                    // ── Enlace de Registro ─────────────────────────────────
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            text  = "¿No tienes una cuenta?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrayMid
                        )
                        TextButton(
                            onClick = onNavigateRegister,
                            enabled = !isLoading
                        ) {
                            Text(
                                text  = "Regístrate",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = GreenPrimary
                            )
                        }
                    }
                }
            }

            // ── Espacio inferior para evitar que el scroll corte la card ──
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}





// ─────────────────────────────────────────────────────────────────────────────
// Colores personalizados para los OutlinedTextField
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun loginTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = GreenPrimary,
    unfocusedBorderColor = GreenLight,
    focusedLabelColor    = GreenPrimary,
    unfocusedLabelColor  = GrayMid,
    cursorColor          = GreenPrimary,
    focusedLeadingIconColor   = GreenPrimary,
    unfocusedLeadingIconColor = GreenLight,
    focusedContainerColor     = GreenSurface.copy(alpha = 0.3f),
    unfocusedContainerColor   = Color.Transparent,
)

// ─────────────────────────────────────────────────────────────────────────────
// PREVIEW
// ─────────────────────────────────────────────────────────────────────────────
@Preview(
    name       = "LoginScreen — Modo Claro",
    showBackground = true,
    showSystemUi   = true
)
@Composable
private fun LoginScreenPreview() {
    FreshEatsTheme {
        LoginScreen(onLoginSuccess = {}, onNavigateRegister = {})
    }
}

@Preview(
    name       = "LoginScreen — Modo Oscuro",
    showBackground = true,
    showSystemUi   = true,
    uiMode     = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun LoginScreenDarkPreview() {
    FreshEatsTheme(darkTheme = true) {
        LoginScreen(onLoginSuccess = {}, onNavigateRegister = {})
    }
}
