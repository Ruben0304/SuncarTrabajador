package com.suncar.suncartrabajador.ui.features.Descripcion

class DescripcionValidator {
    fun validarDescripcion(descripcion: String): Boolean {
        return descripcion.isNotBlank() && descripcion.length >= 10
    }
} 