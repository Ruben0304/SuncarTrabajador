package com.suncar.suncartrabajador.domain.models

import com.google.gson.annotations.SerializedName

data class Brigada(
    @SerializedName("lider")
    val lider: TeamMember,
    @SerializedName("integrantes")
    val integrantes: List<TeamMember>,
    /**
     * Campo opcional que puede venir del backend.
     * Si no está presente, se puede inferir desde lider.id
     */
    @SerializedName("lider_ci")
    val liderCi: String? = null,
    /**
     * Campos opcionales de MongoDB que pueden venir del backend
     */
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("id")
    val idAlt: String? = null
)