package com.suncar.suncartrabajador.data.services

import com.suncar.suncartrabajador.data.schemas.ClienteUpdateRequest
import com.suncar.suncartrabajador.data.schemas.ClienteUpdateResponse
import com.suncar.suncartrabajador.data.schemas.ClienteVerificacionApiResponse
import com.suncar.suncartrabajador.data.schemas.ClienteListItemResponse
import com.suncar.suncartrabajador.domain.models.Cliente
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface ClienteApiService {
    @PATCH("api/clientes/{numero}")
    suspend fun actualizarCliente(
        @Path("numero") numero: String,
        @Body cliente: ClienteUpdateRequest
    ): ClienteUpdateResponse

    @GET("api/clientes/{numero}/verificar")
    suspend fun verificarCliente(@Path("numero") numero: String): ClienteVerificacionApiResponse

    @GET("api/clientes/")
    suspend fun listarClientes(@Query("nombre") nombre: String? = null): List<ClienteListItemResponse>
}
