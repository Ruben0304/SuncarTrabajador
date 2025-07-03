package com.suncar.suncartrabajador.domain.models

import com.google.gson.annotations.SerializedName

data class Brigada(
    @SerializedName("lider")
    val lider: TeamMember,
    @SerializedName("integrantes")
    val integrantes: List<TeamMember>
)