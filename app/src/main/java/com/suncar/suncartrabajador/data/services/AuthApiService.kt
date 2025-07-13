package com.suncar.suncartrabajador.data.services

import com.suncar.suncartrabajador.data.schemas.LoginRequest
import com.suncar.suncartrabajador.data.schemas.LoginResponse
import com.suncar.suncartrabajador.data.schemas.ChangePasswordRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/auth/cambiar_contrasena")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Boolean
} 