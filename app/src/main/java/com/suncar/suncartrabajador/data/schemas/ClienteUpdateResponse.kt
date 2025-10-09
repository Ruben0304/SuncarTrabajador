package com.suncar.suncartrabajador.data.schemas

import com.google.gson.annotations.SerializedName

data class ClienteUpdateResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String
)
