package com.suncar.suncartrabajador.data.services

import com.suncar.suncartrabajador.data.schemas.MantenimientoRequest
import com.suncar.suncartrabajador.data.schemas.MantenimientoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.POST

interface MantenimientoApiService {
    @Multipart
    @POST("api/reportes/mantenimiento")
    suspend fun enviarMantenimiento(
        @Part("tipo_reporte") tipoReporte: RequestBody,
        @Part("brigada") brigada: RequestBody,
        @Part("materiales") materiales: RequestBody,
        @Part("cliente") cliente: RequestBody,
        @Part("fecha_hora") fechaHora: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part fotos_inicio: List<MultipartBody.Part>,
        @Part fotos_fin: List<MultipartBody.Part>
    ): MantenimientoResponse
} 