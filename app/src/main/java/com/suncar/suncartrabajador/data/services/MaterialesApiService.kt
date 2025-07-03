package com.suncar.suncartrabajador.data.services

import com.suncar.suncartrabajador.domain.models.MaterialCategory
import com.suncar.suncartrabajador.domain.models.MaterialProduct
import retrofit2.http.GET
import retrofit2.http.Path

interface MaterialesApiService {
    @GET("api/categorias")
    suspend fun getCategorias(): List<MaterialCategory>

    @GET("api/categorias/{categoria}/materiales")
    suspend fun getMaterialesByCategoria(@Path("categoria") categoria: String): List<MaterialProduct>
}
