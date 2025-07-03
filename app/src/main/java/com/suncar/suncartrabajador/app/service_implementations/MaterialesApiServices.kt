package com.suncar.suncartrabajador.app.service_implementations

import com.suncar.suncartrabajador.data.http.RetrofitClient
import com.suncar.suncartrabajador.data.services.MaterialesApiService

class MaterialesApiServices {
    val api: MaterialesApiService = RetrofitClient.createService()
}