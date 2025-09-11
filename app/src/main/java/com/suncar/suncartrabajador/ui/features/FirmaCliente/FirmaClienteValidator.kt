package com.suncar.suncartrabajador.ui.features.FirmaCliente

class FirmaClienteValidator {
    fun isValid(state: FirmaClienteState): Boolean {
        return state.signatureUri != null
    }
}
