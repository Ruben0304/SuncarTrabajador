package com.suncar.suncartrabajador.ui.screens.Cuenta

import com.suncar.suncartrabajador.domain.models.TeamMember

data class CuentaScreenState(
    val teamMembers: List<TeamMember> = emptyList(),
    val availableTeamMembers: List<TeamMember> = emptyList(),
    val isLoading: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val showChangePasswordDialog: Boolean = false,
    val isChangingPassword: Boolean = false,
    val changePasswordSuccess: Boolean? = null,
    val changePasswordError: String? = null
) 