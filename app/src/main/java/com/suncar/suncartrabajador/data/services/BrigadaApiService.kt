package com.suncar.suncartrabajador.data.services

import com.suncar.suncartrabajador.data.schemas.BrigadasResponse
import retrofit2.http.GET

interface BrigadaApiService {
    @GET("api/brigadas/")
    suspend fun getBrigadas(): BrigadasResponse
}

