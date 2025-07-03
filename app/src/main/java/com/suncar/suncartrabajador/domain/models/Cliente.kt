package com.suncar.suncartrabajador.domain.models


data class Cliente(
    val numero: String,
    val nombre: String,
    val direccion: String? = null
)