package com.suncar.suncartrabajador.ui.screens.Login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suncar.suncartrabajador.data.repositories.AuthRepository
import com.suncar.suncartrabajador.data.repositories.LoginResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()
    private val repository = AuthRepository()

    fun updateCi(ci: String) {
        _uiState.update { it.copy(ci = ci, errorMessage = null) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun login() {
        val currentState = _uiState.value
        
        // Usar el LoginValidator para validar el formulario
        when (val validationResult = LoginValidator.validateLoginForm(currentState.ci, currentState.password)) {
            is ValidationResult.Error -> {
                _uiState.update { it.copy(errorMessage = validationResult.message) }
                return
            }
            is ValidationResult.Success -> {
                // Continuar con el login si la validación es exitosa
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                val result = repository.login(currentState.ci, currentState.password)
                
                when (result) {
                    is LoginResult.Success -> {
                        // Login exitoso - aquí podrías navegar a la siguiente pantalla
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                        // TODO: Implementar navegación a la pantalla principal
                    }
                    is LoginResult.Error -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error de conexión. Intente nuevamente."
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
} 