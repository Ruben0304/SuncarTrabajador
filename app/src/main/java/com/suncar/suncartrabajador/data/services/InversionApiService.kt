package com.suncar.suncartrabajador.data.services

import com.suncar.suncartrabajador.data.schemas.InversionResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface InversionApiService {
    @Multipart
    @POST("api/reportes/inversion")
    suspend fun enviarInversion(
        @Part("tipo_reporte") tipoReporte: RequestBody,
        @Part("brigada") brigada: RequestBody,
        @Part("materiales") materiales: RequestBody,
        @Part("cliente") cliente: RequestBody,
        @Part("fecha_hora") fechaHora: RequestBody,
        @Part fotos_inicio: List<MultipartBody.Part>, // Cambiar nombre
        @Part fotos_fin: List<MultipartBody.Part>, // Cambiar nombre
        @Part firma_cliente: MultipartBody.Part?
    ): InversionResponse
}
