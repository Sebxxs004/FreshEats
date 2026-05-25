package com.fresheats.app.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// ─────────────────────────────────────────────────────────────────────────────
// AuthState — Estado de Autenticación para la UI
// ─────────────────────────────────────────────────────────────────────────────
sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

// ─────────────────────────────────────────────────────────────────────────────
// AuthViewModel — Gestión de Firebase Authentication
// ─────────────────────────────────────────────────────────────────────────────
class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    // Estado de la sesión actual (se actualiza automáticamente en init)
    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    // Estado de la UI para Login/Registro (Loading, Success, Error)
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // Escucha los cambios de sesión en Firebase (por si expira el token o se cierra sesión)
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    /** Iniciar Sesión con Correo y Contraseña */
    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.signInWithEmailAndPassword(email, pass).await()
                result.user?.let {
                    _authState.value = AuthState.Success(it)
                } ?: run {
                    _authState.value = AuthState.Error("Error desconocido al iniciar sesión")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(mapFirebaseAuthError(e))
            }
        }
    }

    /** Registrar Usuario con Correo y Contraseña */
    fun register(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, pass).await()
                result.user?.let {
                    _authState.value = AuthState.Success(it)
                } ?: run {
                    _authState.value = AuthState.Error("Error desconocido al registrar usuario")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(mapFirebaseAuthError(e))
            }
        }
    }

    /** Cerrar Sesión */
    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }

    /** Limpiar el estado de error (útil después de mostrar un Toast) */
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }

    /** Mapea excepciones comunes de Firebase a mensajes amigables en español */
    private fun mapFirebaseAuthError(e: Exception): String {
        return when (val msg = e.message ?: "") {
            msg.contains("ERROR_INVALID_EMAIL", ignoreCase = true) -> "El formato del correo es inválido."
            msg.contains("ERROR_USER_NOT_FOUND", ignoreCase = true) -> "No existe un usuario con este correo."
            msg.contains("ERROR_WRONG_PASSWORD", ignoreCase = true) -> "Contraseña incorrecta."
            msg.contains("ERROR_EMAIL_ALREADY_IN_USE", ignoreCase = true) -> "Este correo ya está registrado."
            msg.contains("ERROR_WEAK_PASSWORD", ignoreCase = true) -> "La contraseña debe tener al menos 6 caracteres."
            msg.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) -> "Credenciales inválidas. Verifica tu correo y contraseña."
            else -> "Error de autenticación: ${e.localizedMessage}"
        }
    }
}
