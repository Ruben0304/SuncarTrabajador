package com.suncar.suncartrabajador.ui.reportes.Mantenimiento

import com.suncar.suncartrabajador.ui.features.Adjuntos.AdjuntosState
import com.suncar.suncartrabajador.ui.features.Materiales.MaterialesState

/**
 * Validators específicos para la pantalla de Mantenimiento
 * En mantenimiento, los materiales y fotos son opcionales
 */
class MantenimientoAdjuntosValidator {
    fun isValid(state: AdjuntosState): Boolean {
        // En mantenimiento, las fotos son opcionales
        return true
    }
}

class MantenimientoMaterialesValidator {
    fun isValid(state: MaterialesState): Boolean {
        // En mantenimiento, los materiales son opcionales
        // Si se agregan materiales, deben estar completos
        return state.materials.isEmpty() || state.materials.all {
            it.type.isNotBlank() &&
            it.name.isNotBlank() &&
            it.quantity.isNotBlank()
        }
    }
} 