package com.suncar.suncartrabajador.app.service_implementations

import com.suncar.suncartrabajador.data.http.RetrofitClient
import com.suncar.suncartrabajador.data.services.UpdateApiService

class UpdateApiServices {
    val api: UpdateApiService = RetrofitClient.createService()
}
