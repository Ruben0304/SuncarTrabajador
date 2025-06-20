package com.suncar.suncartrabajador.ui.features.Materiales

class MaterialesValidator {
    fun isValid(state: MaterialesState): Boolean {
        return state.materials.all {
            it.type.isNotBlank() &&
            it.name.isNotBlank() &&
            it.quantity.isNotBlank()
        }
    }
} 