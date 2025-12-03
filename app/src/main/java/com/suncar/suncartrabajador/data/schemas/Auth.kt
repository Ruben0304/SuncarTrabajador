package com.suncar.suncartrabajador.data.schemas

import com.google.gson.annotations.SerializedName
import com.suncar.suncartrabajador.domain.models.Brigada

data class LoginRequest(
    @SerializedName("ci")
    val ci: String,
    @SerializedName("contraseña")
    val contraseña: String
)

data class LoginUserResponse(
    @SerializedName("ci")
    val ci: String,
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("rol")
    val rol: String
)

data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("token")
    val token: String? = null,
    @SerializedName("user")
    val user: LoginUserResponse? = null,
    /**
     * Campo legado para compatibilidad con el endpoint antiguo.
     * El nuevo endpoint puede no devolverlo.
     */
    @SerializedName("brigada")
    val brigada: Brigada? = null
)

data class ChangePasswordRequest(
    @SerializedName("ci")
    val ci: String,
    @SerializedName("nueva_contrasena")
    val nuevaContrasena: String
)