package com.suncar.suncartrabajador.ui.screens.Cuenta

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suncar.suncartrabajador.domain.models.TeamMember
import com.suncar.suncartrabajador.singleton.Auth
import com.suncar.suncartrabajador.singleton.Trabajadores
import com.suncar.suncartrabajador.ui.screens.Login.LoginViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.suncar.suncartrabajador.app.service_implementations.AuthService

class CuentaScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CuentaScreenState())
    val uiState: StateFlow<CuentaScreenState> = _uiState.asStateFlow()

    private val authService = AuthService()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Obtener la brigada actual desde Auth
            val currentBrigada = Auth.getCurrentBrigada()
            val currentIntegrantes = currentBrigada?.integrantes ?: emptyList()
            
            // Obtener todos los trabajadores disponibles desde Trabajadores
            val allTrabajadores = Trabajadores.getTrabajadores()
            
            // Filtrar trabajadores que no están en la brigada actual
            val availableTrabajadores = allTrabajadores.filterNot { trabajador ->
                currentIntegrantes.any { integrante -> integrante.id == trabajador.id }
            }
            
            _uiState.update {
                it.copy(
                    teamMembers = currentIntegrantes,
                    availableTeamMembers = availableTrabajadores,
                    isLoading = false
                )
            }
        }
    }

    fun addTeamMember(member: TeamMember) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                val currentMembers = currentState.teamMembers
                
                // Verificar que el miembro no esté ya en la brigada
                if (currentMembers.none { it.id == member.id }) {
                    val newTeamMembers = currentMembers + member
                    
                    // Actualizar la brigada en Auth
                    updateBrigadaInAuth(newTeamMembers)
                    
                    // Filtrar trabajadores disponibles excluyendo los que están en la brigada
                    val allTrabajadores = Trabajadores.getTrabajadores()
                    val availableTrabajadores = allTrabajadores.filterNot { trabajador ->
                        newTeamMembers.any { integrante -> integrante.id == trabajador.id }
                    }
                    
                    currentState.copy(
                        teamMembers = newTeamMembers,
                        availableTeamMembers = availableTrabajadores
                    )
                } else {
                    currentState
                }
            }
        }
    }

    fun removeTeamMember(member: TeamMember) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                val newTeamMembers = currentState.teamMembers.filterNot { tm -> tm.id == member.id }
                
                // Actualizar la brigada en Auth
                updateBrigadaInAuth(newTeamMembers)
                
                // Filtrar trabajadores disponibles excluyendo los que están en la brigada
                val allTrabajadores = Trabajadores.getTrabajadores()
                val availableTrabajadores = allTrabajadores.filterNot { trabajador ->
                    newTeamMembers.any { integrante -> integrante.id == trabajador.id }
                }
                
                currentState.copy(
                    teamMembers = newTeamMembers,
                    availableTeamMembers = availableTrabajadores
                )
            }
        }
    }
    
    private fun updateBrigadaInAuth(newIntegrantes: List<TeamMember>) {
        val currentUser = Auth.getCurrentUser()
        val currentBrigada = Auth.getCurrentBrigada()
        
        if (currentUser != null && currentBrigada != null) {
            // Crear una nueva brigada con los integrantes actualizados
            val updatedBrigada = currentBrigada.copy(integrantes = newIntegrantes)
            
            // Crear un nuevo usuario con la brigada actualizada
            val updatedUser = currentUser.copy(brigada = updatedBrigada)
            
            // Actualizar el usuario en Auth
            Auth.setUser(updatedUser)
        }
    }

    /**
     * Muestra el diálogo de confirmación de logout
     */
    fun showLogoutDialog() {
        _uiState.update { it.copy(showLogoutDialog = true) }
    }

    /**
     * Oculta el diálogo de confirmación de logout
     */
    fun hideLogoutDialog() {
        _uiState.update { it.copy(showLogoutDialog = false) }
    }

    /**
     * Cierra la sesión del usuario actual
     */
    fun logout(context: Context, onLogoutComplete: () -> Unit) {
        LoginViewModel.logoutFromAnywhere(context)
        hideLogoutDialog()
        onLogoutComplete()
    }

    fun showChangePasswordDialog() {
        _uiState.update { it.copy(showChangePasswordDialog = true, changePasswordSuccess = null, changePasswordError = null) }
    }

    fun hideChangePasswordDialog() {
        _uiState.update { it.copy(showChangePasswordDialog = false, isChangingPassword = false, changePasswordSuccess = null, changePasswordError = null) }
    }

    fun changePassword(newPassword: String) {
        val user = Auth.getCurrentUser()
        if (user == null) {
            _uiState.update { it.copy(changePasswordError = "Usuario no autenticado") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isChangingPassword = true, changePasswordSuccess = null, changePasswordError = null) }
            val result = authService.changePassword(user.ci, newPassword)
            if (result) {
                // Actualizar el usuario en Auth con la nueva contraseña
                Auth.setUser(user.copy(password = newPassword))
                _uiState.update { it.copy(isChangingPassword = false, changePasswordSuccess = true, showChangePasswordDialog = false) }
            } else {
                _uiState.update { it.copy(isChangingPassword = false, changePasswordSuccess = false, changePasswordError = "No se pudo cambiar la contraseña. Intenta de nuevo.") }
            }
        }
    }
} 