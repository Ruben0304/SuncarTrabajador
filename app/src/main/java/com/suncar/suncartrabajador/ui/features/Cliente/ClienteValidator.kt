package com.suncar.suncartrabajador.ui.features.Cliente

class ClienteValidator {
    fun isValid(state: ClienteState): Boolean {
        // Validar solo número: debe estar presente y ser numérico
        return state.numero.isNotBlank() && state.numero.all { it.isDigit() }
    }
} 