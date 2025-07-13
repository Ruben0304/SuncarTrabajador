package com.suncar.suncartrabajador.data.schemas

import com.google.gson.annotations.SerializedName
import com.suncar.suncartrabajador.domain.models.Brigada

data class LoginRequest(
    @SerializedName("ci")
    val ci: String,
    @SerializedName("contraseña")
    val contraseña: String
)

data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("brigada")
    val brigada: Brigada? = null
)

data class ChangePasswordRequest(
    @SerializedName("ci")
    val ci: String,
    @SerializedName("nueva_contrasena")
    val nuevaContrasena: String
)