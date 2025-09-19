package com.suncar.suncartrabajador.app.service_implementations

import com.suncar.suncartrabajador.data.http.RetrofitClient
import com.suncar.suncartrabajador.data.services.TrabajadoresApiService

class TrabajadoresApiServices {
    val api: TrabajadoresApiService = RetrofitClient.createService()
}