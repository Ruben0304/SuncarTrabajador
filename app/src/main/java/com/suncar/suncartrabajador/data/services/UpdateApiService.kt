package com.suncar.suncartrabajador.data.services

import com.suncar.suncartrabajador.domain.models.UpdateInfo
import com.suncar.suncartrabajador.domain.models.UpdateRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface UpdateApiService {
    @POST("api/update/application") suspend fun checkForUpdates(@Body request: UpdateRequest): UpdateInfo
}
