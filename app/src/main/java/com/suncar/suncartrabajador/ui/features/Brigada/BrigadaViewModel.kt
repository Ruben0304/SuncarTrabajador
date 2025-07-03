package com.suncar.suncartrabajador.ui.features.Brigada

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suncar.suncartrabajador.domain.models.TeamMember
import com.suncar.suncartrabajador.singleton.Auth
import com.suncar.suncartrabajador.singleton.Trabajadores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BrigadaViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BrigadaState())
    val uiState: StateFlow<BrigadaState> = _uiState.asStateFlow()

    private val validator = BrigadaValidator()

    init {
        loadBrigadaData()
    }

    /**
     * Carga los datos de la brigada desde el singleton Auth
     * SOLO CARGA, NO MODIFICA los datos
     */
    private fun loadBrigadaData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val currentBrigada = Auth.getCurrentBrigada()
                val integrantes = Auth.getIntegrantes()
                val disponibles = Trabajadores.getTrabajadores()
                
                if (currentBrigada != null) {
                    // Cargar líder y miembros de la brigada actual
                    _uiState.update {
                        it.copy(
                            leader = currentBrigada.lider,
                            teamMembers = integrantes,
                            availableTeamMembers = getAvailableTeamMembers(disponibles, integrantes),
                            availableLeaders = disponibles,
                            isLoading = false
                        )
                    }
                } else {
                    // Si no hay brigada actual, mostrar estado vacío
                    _uiState.update {
                        it.copy(
                            leader = null,
                            teamMembers = emptyList(),
                            availableTeamMembers = disponibles,
                            availableLeaders = disponibles,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                // En caso de error, mantener estado vacío
                _uiState.update {
                    it.copy(
                        leader = null,
                        teamMembers = emptyList(),
                        availableTeamMembers = emptyList(),
                        availableLeaders = emptyList(),
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * Obtiene los trabajadores disponibles excluyendo los que ya están seleccionados como miembros del equipo
     */
    private fun getAvailableTeamMembers(allTrabajadores: List<TeamMember>, selectedMembers: List<TeamMember>): List<TeamMember> {
        val selectedIds = selectedMembers.map { it.id }.toSet()
        return allTrabajadores.filter { it.id !in selectedIds }
    }

    /**
     * Actualiza dinámicamente la lista de trabajadores disponibles
     */
    private fun updateAvailableTeamMembers() {
        val currentState = _uiState.value
        val allTrabajadores = Trabajadores.getTrabajadores()
        val availableMembers = getAvailableTeamMembers(allTrabajadores, currentState.teamMembers)
        
        _uiState.update { it.copy(availableTeamMembers = availableMembers) }
    }

    /**
     * Selecciona un líder para la brigada (solo para el formulario)
     */
    fun selectLeader(leader: TeamMember) {
        _uiState.update { it.copy(leader = leader) }
    }

    /**
     * Añade un miembro al equipo (solo para el formulario)
     */
    fun addTeamMember(member: TeamMember) {
        _uiState.update { currentState ->
            val currentMembers = currentState.teamMembers
            if (currentMembers.none { it.id == member.id }) {
                currentState.copy(teamMembers = currentMembers + member)
            } else {
                currentState
            }
        }
        // Actualizar dinámicamente la lista de disponibles
        updateAvailableTeamMembers()
    }

    /**
     * Elimina un miembro del equipo (solo para el formulario)
     */
    fun removeTeamMember(member: TeamMember) {
        _uiState.update { currentState ->
            val newTeamMembers = currentState.teamMembers.filterNot { it.id == member.id }
            currentState.copy(teamMembers = newTeamMembers)
        }
        // Actualizar dinámicamente la lista de disponibles
        updateAvailableTeamMembers()
    }

    /**
     * Valida si el estado actual es válido para enviar
     */
    fun isValidState(): Boolean {
        return validator.isValid(_uiState.value)
    }

    /**
     * Obtiene los datos actuales para enviar en el formulario
     */
    fun getFormData(): BrigadaFormData {
        val currentState = _uiState.value
        return BrigadaFormData(
            leader = currentState.leader,
            teamMembers = currentState.teamMembers
        )
    }

    /**
     * Recarga los datos desde Auth
     */
    fun refreshData() {
        loadBrigadaData()
    }
}

/**
 * Datos del formulario para ser enviados
 */
data class BrigadaFormData(
    val leader: TeamMember?,
    val teamMembers: List<TeamMember>
)
