package com.suncar.suncartrabajador.data.services

import com.suncar.suncartrabajador.data.schemas.TrabajadorResponse
import com.suncar.suncartrabajador.data.schemas.TrabajadoresResponse
import com.suncar.suncartrabajador.domain.models.TeamMember
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TrabajadoresApiService {
    @GET("api/trabajadores/") suspend fun getTrabajadores(): TrabajadoresResponse

    @POST("api/trabajadores")
    suspend fun addTrabajador(@Body trabajador: TeamMember): TrabajadorResponse
}
