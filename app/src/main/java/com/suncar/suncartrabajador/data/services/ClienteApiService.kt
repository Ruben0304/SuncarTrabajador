package com.suncar.suncartrabajador.data.services

import com.suncar.suncartrabajador.data.schemas.ClienteCreateRequest
import com.suncar.suncartrabajador.data.schemas.ClienteVerificacionApiResponse
import com.suncar.suncartrabajador.domain.models.Cliente
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ClienteApiService {
    @POST("api/clientes") suspend fun crearCliente(@Body cliente: ClienteCreateRequest): Cliente

    @GET("api/clientes/{numero}/verificar")
    suspend fun verificarCliente(@Path("numero") numero: String): ClienteVerificacionApiResponse
}
