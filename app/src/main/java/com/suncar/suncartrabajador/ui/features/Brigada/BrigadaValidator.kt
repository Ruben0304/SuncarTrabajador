package com.suncar.suncartrabajador.ui.features.Brigada

class BrigadaValidator {
    fun isValid(state: BrigadaState): Boolean {
        return state.leader != null &&
               state.teamMembers.isNotEmpty() &&
               state.teamMembers.all { it.name.isNotBlank() && it.id.isNotBlank() }
    }
} 