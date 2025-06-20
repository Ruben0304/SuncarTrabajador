package com.suncar.suncartrabajador.ui.features.Brigada

import com.suncar.suncartrabajador.domain.models.TeamMember


data class BrigadaState(
    val leader: TeamMember? = null,
    val teamMembers: List<TeamMember> = emptyList(),
    val availableTeamMembers: List<TeamMember> = emptyList(),
    val availableLeaders: List<TeamMember> = emptyList(),
    val isLoading: Boolean = false
) 