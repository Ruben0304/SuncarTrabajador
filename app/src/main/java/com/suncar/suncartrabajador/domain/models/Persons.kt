package com.suncar.suncartrabajador.domain.models

import com.google.gson.annotations.SerializedName


data class TeamMember(
    @SerializedName("nombre")
    val name: String = "",
    @SerializedName("CI")
    val id: String = "",
    @SerializedName("tiene_contraseña")
    val hasPassword: Boolean = false,
    @SerializedName("is_brigadista")
    val isBrigadista: Boolean = false
)