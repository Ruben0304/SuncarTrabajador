package com.suncar.suncartrabajador.ui.screens.Cuenta

class CuentaScreenValidator {
    fun isValid(state: CuentaScreenState): Boolean {
        return state.teamMembers.isNotEmpty() &&
                state.teamMembers.all { it.name.isNotBlank() && it.id.isNotBlank() }
    }
} 