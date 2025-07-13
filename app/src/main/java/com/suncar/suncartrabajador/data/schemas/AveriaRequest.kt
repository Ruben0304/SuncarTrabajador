package com.suncar.suncartrabajador.data.schemas

import com.google.gson.annotations.SerializedName
import com.suncar.suncartrabajador.domain.models.TeamMember
import com.suncar.suncartrabajador.domain.models.MaterialItem
import com.suncar.suncartrabajador.domain.models.LocationData
import java.time.LocalDate
import java.time.LocalTime

/**
 * Request schema para el reporte de avería
 * Contiene todos los datos capturados en el formulario de avería
 */
data class AveriaRequest(
    @SerializedName("tipo_reporte")
    val tipoReporte: String = "averia",

    @SerializedName("brigada")
    val brigada: BrigadaRequest,

    @SerializedName("materiales")
    val materiales: List<MaterialRequest>,

    @SerializedName("cliente")
    val cliente: ClienteCreateRequest,

    @SerializedName("fecha_hora")
    val fechaHora: FechaHoraRequest,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("adjuntos")
    val adjuntos: AdjuntosRequest,

    @SerializedName("fecha_creacion")
    val fechaCreacion: String = LocalDate.now().toString(),
) 