package com.suncar.suncartrabajador.app.service_implementations

import com.suncar.suncartrabajador.data.http.RetrofitClient
import com.suncar.suncartrabajador.data.schemas.LoginRequest
import com.suncar.suncartrabajador.data.schemas.LoginResponse
import com.suncar.suncartrabajador.data.services.AuthApiService
import com.suncar.suncartrabajador.domain.models.User
import com.suncar.suncartrabajador.singleton.Auth
import java.io.IOException

class AuthService {
    private val authApiService: AuthApiService = RetrofitClient.createService()

    suspend fun login(ci: String, contraseña: String): LoginResponse {
        return try {
            val request = LoginRequest(ci, contraseña)
            val response = authApiService.login(request)

            if (response.success && response.brigada != null) {
                // Crear el usuario con la información de la brigada
                val user = User(
                    ci = response.brigada.lider.id,
                    name = response.brigada.lider.name,
                    password = contraseña,
                    brigada = response.brigada
                )
                Auth.setUser(user)
            }
            response

        } catch (e: IOException) {
            LoginResponse(success = false, message = "Error de conexión: Verifique su conexión a internet")
        } catch (e: Exception) {
            LoginResponse(success = false, message = "Error inesperado: ${e.message}")
        }
    }
}