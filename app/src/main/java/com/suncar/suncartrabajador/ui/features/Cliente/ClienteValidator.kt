package com.suncar.suncartrabajador.ui.features.Cliente

class ClienteValidator {
    fun isValid(state: ClienteState): Boolean {
        return if (state.esClienteNuevo) {
            // Para cliente nuevo: validar número y nombre
            state.numeroClienteNuevo.isNotBlank() && 
            state.numeroClienteNuevo.all { it.isDigit() } &&
            state.nombreClienteNuevo.isNotBlank()
        } else {
            // Para cliente existente: validar solo número
            state.numero.isNotBlank() && state.numero.all { it.isDigit() }
        }
    }
} 