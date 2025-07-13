package com.suncar.suncartrabajador.data.services

import com.suncar.suncartrabajador.data.schemas.CategoriaListResponse
import com.suncar.suncartrabajador.data.schemas.ProductoListResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface MaterialesApiService {
    @GET("api/productos/categorias") suspend fun getCategorias(): CategoriaListResponse

    @GET("api/productos/categorias/{categoria}/materiales")
    suspend fun getMaterialesByCategoria(@Path("categoria") categoria: String): ProductoListResponse
}
