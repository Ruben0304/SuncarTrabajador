package com.suncar.suncartrabajador.data.schemas

import com.google.gson.annotations.SerializedName
import com.suncar.suncartrabajador.domain.models.Cliente


data class ClienteCreateResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: Cliente
)
