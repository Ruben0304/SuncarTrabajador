package com.suncar.suncartrabajador.ui.features.Brigada

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suncar.suncartrabajador.data.repositories.BrigadaRepository
import com.suncar.suncartrabajador.domain.models.TeamMember
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BrigadaViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BrigadaState())
    val uiState: StateFlow<BrigadaState> = _uiState.asStateFlow()
    private val repository = BrigadaRepository()
    private var allTeamMembers: List<TeamMember> = emptyList()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val initialData = repository.getInitialBrigadaData()
            allTeamMembers = initialData.allMembers
            _uiState.update {
                it.copy(
                    isLoading = false,
                    leader = initialData.leader,
                    teamMembers = initialData.brigade,
                    availableTeamMembers = initialData.availableMembers,
                    availableLeaders = initialData.availableLeaders
                )
            }
        }
    }

    fun selectLeader(leader: TeamMember) {
        _uiState.update { currentState ->
            // The new leader cannot be a regular member
            val newTeamMembers = currentState.teamMembers.filterNot { it.id == leader.id }
            currentState.copy(
                leader = leader,
                teamMembers = newTeamMembers,
                availableTeamMembers = allTeamMembers.filterNot { it.id == leader.id },
                availableLeaders = allTeamMembers.filter { m -> newTeamMembers.none { it.id == m.id } }
            )
        }
    }

    fun addTeamMember(member: TeamMember) {
        _uiState.update {
            val currentMembers = it.teamMembers
            if (currentMembers.none { it.id == member.id } && it.leader?.id != member.id) {
                val newTeamMembers = currentMembers + member
                it.copy(
                    teamMembers = newTeamMembers,
                    availableLeaders = allTeamMembers.filter { m -> newTeamMembers.none { tm -> tm.id == m.id } }
                )
            } else {
                it
            }
        }
    }

    fun removeTeamMember(member: TeamMember) {
        _uiState.update {
            val newTeamMembers = it.teamMembers.filterNot { it.id == member.id }
            it.copy(
                teamMembers = newTeamMembers,
                availableLeaders = allTeamMembers.filter { m -> newTeamMembers.none { tm -> tm.id == m.id } }
            )
        }
    }

    fun updateTeamMember(oldMember: TeamMember, newMember: TeamMember) {
        val currentMembers = _uiState.value.teamMembers
        _uiState.value = _uiState.value.copy(
            teamMembers = currentMembers.map { if (it == oldMember) newMember else it }
        )
    }
} 