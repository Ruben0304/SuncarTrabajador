package com.suncar.suncartrabajador.data.schemas

import com.google.gson.annotations.SerializedName

/**
 * Response schema para el reporte de avería
 */
data class AveriaResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: AveriaData
)

/**
 * Datos del reporte de mantenimiento en la respuesta
 */
data class AveriaData(
    @SerializedName("tipo_reporte")
    val tipoReporte: String,

    @SerializedName("brigada")
    val brigada: BrigadaResponse,

    @SerializedName("materiales")
    val materiales: List<MaterialResponse>,

    @SerializedName("ubicacion")
    val ubicacion: UbicacionResponse,

    @SerializedName("cliente")
    val cliente: ClienteResponse?,

    @SerializedName("fecha_hora")
    val fechaHora: FechaHoraResponse,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("adjuntos")
    val adjuntos: AdjuntosResponse
) 