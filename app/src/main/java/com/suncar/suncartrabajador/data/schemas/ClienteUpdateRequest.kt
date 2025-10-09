package com.suncar.suncartrabajador.data.schemas

import com.google.gson.annotations.SerializedName

data class ClienteUpdateRequest(
    @SerializedName("nombre")
    val nombre: String? = null,
    @SerializedName("direccion")
    val direccion: String? = null,
    @SerializedName("telefono")
    val telefono: String? = null,
    @SerializedName("latitud")
    val latitud: Double? = null,
    @SerializedName("longitud")
    val longitud: Double? = null
)
