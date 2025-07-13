package com.suncar.suncartrabajador.domain.models
import com.suncar.suncartrabajador.domain.models.Brigada

data class User(
    val ci: String,
    val name: String,
    val password: String,
    val brigada: Brigada? = null
)


