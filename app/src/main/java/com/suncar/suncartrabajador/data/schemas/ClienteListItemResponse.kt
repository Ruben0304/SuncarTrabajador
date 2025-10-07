package com.suncar.suncartrabajador.data.schemas

import com.google.gson.annotations.SerializedName

data class ClienteListItemResponse(
    @SerializedName("numero") val numero: String?,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("direccion") val direccion: String?,
    @SerializedName("id") val id: String? = null
)
