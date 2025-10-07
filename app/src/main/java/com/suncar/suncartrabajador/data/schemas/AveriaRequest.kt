package com.suncar.suncartrabajador.data.schemas

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

/**
 * Request schema para el reporte de avería Contiene todos los datos capturados en el formulario de
 * avería
 */
data class AveriaRequest(
        @SerializedName("tipo_reporte") val tipoReporte: String = "averia",
        @SerializedName("brigada") val brigada: BrigadaRequest,
        @SerializedName("materiales") val materiales: List<MaterialRequest>,
        @SerializedName("cliente") val cliente: ClienteCreateRequest,
        @SerializedName("fecha_hora") val fechaHora: FechaHoraRequest,
        @SerializedName("descripcion") val descripcion: String,
        @SerializedName("adjuntos") val adjuntos: AdjuntosRequest,
        @SerializedName("firma_cliente") val firmaCliente: FirmaClienteRequest? = null,
        @SerializedName("fecha_creacion") val fechaCreacion: String = LocalDate.now().toString(),
        @SerializedName("local_id") val localId: Long = System.currentTimeMillis(),
)
