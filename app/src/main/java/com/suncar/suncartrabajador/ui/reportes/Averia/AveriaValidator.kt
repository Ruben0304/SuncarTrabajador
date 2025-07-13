package com.suncar.suncartrabajador.ui.reportes.Averia

import com.suncar.suncartrabajador.ui.features.Adjuntos.AdjuntosState
import com.suncar.suncartrabajador.ui.features.Materiales.MaterialesState

/**
 * Validators específicos para la pantalla de Avería
 * En avería, los materiales y fotos son opcionales
 */
class AveriaAdjuntosValidator {
    fun isValid(state: AdjuntosState): Boolean {
        // En avería, las fotos son opcionales
        return true
    }
}

class AveriaMaterialesValidator {
    fun isValid(state: MaterialesState): Boolean {
        // En avería, los materiales son opcionales
        // Si se agregan materiales, deben estar completos
        return state.materials.isEmpty() || state.materials.all {
            it.type.isNotBlank() &&
            it.name.isNotBlank() &&
            it.quantity.isNotBlank()
        }
    }
} 