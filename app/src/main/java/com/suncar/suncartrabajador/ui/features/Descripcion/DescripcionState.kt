package com.suncar.suncartrabajador.ui.features.Descripcion

data class DescripcionState(
    val descripcion: String = "",
    val isValid: Boolean = false,
    val error: String? = null
) 