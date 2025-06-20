package com.suncar.suncartrabajador.ui.features.Adjuntos

class AdjuntosValidator {
    fun isValid(state: AdjuntosState): Boolean {
        return state.startAttachments.isNotEmpty() && state.endAttachments.isNotEmpty()
    }
} 