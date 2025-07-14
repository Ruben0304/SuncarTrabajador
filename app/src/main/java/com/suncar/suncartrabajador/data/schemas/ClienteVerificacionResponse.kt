package com.suncar.suncartrabajador.data.schemas

import com.google.gson.annotations.SerializedName

data class ClienteVerificacionResponse(
        @SerializedName("existe") val existe: Boolean,
        @SerializedName("nombre") val nombre: String? = null,
        @SerializedName("direccion") val direccion: String? = null,
        @SerializedName("mensaje") val mensaje: String? = null
)

data class ClienteVerificacionApiResponse(
        @SerializedName("success") val success: Boolean,
        @SerializedName("message") val message: String,
        @SerializedName("data") val data: ClienteData?
)

data class ClienteData(
        @SerializedName("numero") val numero: String?,
        @SerializedName("nombre") val nombre: String?,
        @SerializedName("direccion") val direccion: String?,
        @SerializedName("latitud") val latitud: String?,
        @SerializedName("longitud") val longitud: String?
)
