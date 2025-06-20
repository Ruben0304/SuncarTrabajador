package com.suncar.suncartrabajador.data.repositories

import com.suncar.suncartrabajador.domain.models.TeamMember
import kotlinx.coroutines.delay

class BrigadaRepository {

    private val allTeamMembers = listOf(
        TeamMember("Juan Perez", "1"),
        TeamMember("Ana Lopez", "2"),
        TeamMember("Luis Gomez", "3"),
        TeamMember("Maria Fernandez", "4"),
        TeamMember("Carlos Ruiz", "5"),
        TeamMember("Sofia Torres", "6")
    )

    suspend fun getInitialBrigadaData(): InitialBrigadaData {
        delay(1000) // Simulate network delay
        val defaultLeader = allTeamMembers.first()
        val defaultBrigade = allTeamMembers.drop(1).take(2)
        val availableMembers = allTeamMembers.filter { m -> m.id != defaultLeader.id }
        val availableLeaders = allTeamMembers.filter { m -> defaultBrigade.none { it.id == m.id } }
        return InitialBrigadaData(
            leader = defaultLeader,
            brigade = defaultBrigade,
            allMembers = allTeamMembers,
            availableMembers = availableMembers,
            availableLeaders = availableLeaders
        )
    }

    fun getAllTeamMembers(): List<TeamMember> {
        return allTeamMembers
    }
}

data class InitialBrigadaData(
    val leader: TeamMember,
    val brigade: List<TeamMember>,
    val allMembers: List<TeamMember>,
    val availableMembers: List<TeamMember>,
    val availableLeaders: List<TeamMember>
) 