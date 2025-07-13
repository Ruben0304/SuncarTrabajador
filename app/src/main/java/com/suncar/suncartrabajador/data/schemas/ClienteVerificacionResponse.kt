package com.suncar.suncartrabajador.data.schemas

import com.google.gson.annotations.SerializedName

data class ClienteVerificacionResponse(
    @SerializedName("existe")
    val existe: Boolean,
    
    @SerializedName("nombre")
    val nombre: String? = null,
    
    @SerializedName("direccion")
    val direccion: String? = null,
    
    @SerializedName("mensaje")
    val mensaje: String? = null
) 