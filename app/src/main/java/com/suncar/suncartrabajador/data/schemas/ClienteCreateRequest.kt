package com.suncar.suncartrabajador.data.schemas

import com.google.gson.annotations.SerializedName

data class ClienteCreateRequest(
    @SerializedName("numero")
    val numero: String,
    @SerializedName("nombre")
    val nombre: String? = null,
    @SerializedName("direccion")
    val direccion: String? = null,
    @SerializedName("latitud")
    val latitud: String? = null,
    @SerializedName("longitud")
    val longitud: String? = null,
)