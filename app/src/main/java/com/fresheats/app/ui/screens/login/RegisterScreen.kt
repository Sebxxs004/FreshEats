package com.fresheats.app.ui.screens.login

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fresheats.app.R
import com.fresheats.app.ui.theme.GrayMid
import com.fresheats.app.ui.theme.GreenBright
import com.fresheats.app.ui.theme.GreenDark
import com.fresheats.app.ui.theme.GreenLight
import com.fresheats.app.ui.theme.GreenPrimary
import com.fresheats.app.ui.theme.GreenSurface
import com.fresheats.app.ui.theme.White

// ─────────────────────────────────────────────────────────────────────────────
// FreshEats — RegisterScreen
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateLogin:   () -> Unit,
    viewModel:         AuthViewModel = viewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var name            by rememberSaveable { mutableStateOf("") }
    var email           by rememberSaveable { mutableStateOf("") }
    var password        by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // ── Animación de entrada ────────────────────────────────
    var formVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { formVisible = true }
    val formAlpha by animateFloatAsState(
        targetValue = if (formVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 700, easing = EaseOutCubic),
        label = "formAlpha"
    )

    // ── Manejo del estado AuthState ────────────────────────
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> onRegisterSuccess()
            is AuthState.Error -> {
                val errorMsg = (authState as AuthState.Error).message
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
            else -> {}
        }
    }

    val isLoading = authState is AuthState.Loading

    // ── Gradiente de fondo principal ───────────────────────────────────────
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            GreenDark, GreenPrimary, GreenBright, GreenSurface
        ),
        startY = 0f,
        endY = 1200f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
    ) {

        // ── Imagen de fondo hero ───────────────────────────────
        Image(
            painter            = painterResource(id = R.drawable.bg_login_hero),
            contentDescription = null,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier
                .fillMaxWidth()
                .height(460.dp)
                .align(Alignment.TopCenter)
                .alpha(0.22f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // ── Logo circular FreshEats ───────────────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .shadow(elevation = 20.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(White)
            ) {
                Image(
                    painter            = painterResource(id = R.drawable.ic_fresheats_logo),
                    contentDescription = "Logo FreshEats",
                    contentScale       = ContentScale.Fit,
                    modifier           = Modifier.size(86.dp).clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text       = "Únete a Fresh Eats",
                style      = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold, fontSize = 34.sp
                ),
                color      = White,
                textAlign  = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ── Tarjeta de Formulario ─────────────────────────────────────────
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text  = "Crear Cuenta",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = GreenDark
                    )

                    // Nombre
                    OutlinedTextField(
                        value         = name,
                        onValueChange = { name = it },
                        label         = { Text("Nombre Completo") },
                        leadingIcon   = { Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimary) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        singleLine    = true,
                        shape         = RoundedCornerShape(14.dp),
                        colors        = registerTextFieldColors(),
                        modifier      = Modifier.fillMaxWidth(),
                        enabled       = !isLoading
                    )

                    // Correo
                    OutlinedTextField(
                        value         = email,
                        onValueChange = { email = it },
                        label         = { Text("Correo electrónico") },
                        leadingIcon   = { Icon(Icons.Default.Email, contentDescription = null, tint = GreenPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        singleLine    = true,
                        shape         = RoundedCornerShape(14.dp),
                        colors        = registerTextFieldColors(),
                        modifier      = Modifier.fillMaxWidth(),
                        enabled       = !isLoading
                    )

                    // Contraseña
                    OutlinedTextField(
                        value         = password,
                        onValueChange = { password = it },
                        label         = { Text("Contraseña") },
                        leadingIcon   = { Icon(Icons.Default.Lock, contentDescription = null, tint = GreenPrimary) },
                        trailingIcon  = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = GrayMid
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            if(email.isNotBlank() && password.isNotBlank()) {
                                viewModel.register(email, password)
                            }
                        }),
                        singleLine    = true,
                        shape         = RoundedCornerShape(14.dp),
                        colors        = registerTextFieldColors(),
                        modifier      = Modifier.fillMaxWidth(),
                        enabled       = !isLoading
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            if(email.isNotBlank() && password.isNotBlank()) {
                                viewModel.register(email, password)
                            } else {
                                Toast.makeText(context, "Llena todos los campos", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape  = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary, contentColor = White)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = White, strokeWidth = 2.5.dp)
                        } else {
                            Text(
                                text  = "Registrarme",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            )
                        }
                    }

                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            text  = "¿Ya tienes cuenta?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrayMid
                        )
                        TextButton(onClick = onNavigateLogin, enabled = !isLoading) {
                            Text(
                                text  = "Inicia Sesión",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = GreenPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun registerTextFieldColors() = OutlinedTextFieldDefaults.colors(
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
