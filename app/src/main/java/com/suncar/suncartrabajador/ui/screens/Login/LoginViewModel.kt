package com.suncar.suncartrabajador.ui.screens.Login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suncar.suncartrabajador.app.service_implementations.AuthService
import com.suncar.suncartrabajador.data.local.SessionManager
import com.suncar.suncartrabajador.data.schemas.LoginResponse
import com.suncar.suncartrabajador.singleton.Auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()
    private val authService = AuthService()
    private var sessionManager: SessionManager? = null

    fun initializeSessionManager(context: Context) {
        sessionManager = SessionManager(context)
    }

    fun updateCi(ci: String) {
        _uiState.update { it.copy(ci = ci, errorMessage = null) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun login(function: () -> Unit) {
        val currentState = _uiState.value
        
        // Usar el LoginValidator para validar el formulario
        when (val validationResult = LoginValidator.validateLoginForm(currentState.ci, currentState.password)) {
            is ValidationResult.Error -> {
                _uiState.update { it.copy(errorMessage = validationResult.message, loginSuccess = false) }
                return
            }
            is ValidationResult.Success -> {

            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, loginSuccess = false) }
            
            try {
                val response: LoginResponse = authService.login(currentState.ci, currentState.password)
                
                if (response.success && response.brigada != null) {
                    // Guardar sesión automáticamente
                    sessionManager?.saveSession(currentState.ci, currentState.password)
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = null,
                            loginSuccess = true
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = response.message,
                            loginSuccess = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error inesperado. ${e.message}",
                        loginSuccess = false
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Verifica si hay una sesión almacenada y la carga automáticamente
     */
    fun checkStoredSession(): Boolean {
        val session = sessionManager?.getSession()
        return if (session != null) {
            _uiState.update { 
                it.copy(
                    ci = session.ci,
                    password = session.password
                )
            }
            true
        } else {
            false
        }
    }

    /**
     * Cierra la sesión almacenada
     */
    fun logout() {
        sessionManager?.clearSession()
        Auth.logout()
    }

    /**
     * Método estático para cerrar sesión desde cualquier parte de la aplicación
     */
    companion object {
        fun logoutFromAnywhere(context: Context) {
            val sessionManager = SessionManager(context)
            sessionManager.clearSession()
            Auth.logout()
        }
    }
} 