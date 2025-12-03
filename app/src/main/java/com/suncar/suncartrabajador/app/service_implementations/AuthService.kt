package com.suncar.suncartrabajador.app.service_implementations

import com.suncar.suncartrabajador.data.http.RetrofitClient
import com.suncar.suncartrabajador.data.local.AuthPreferences
import com.suncar.suncartrabajador.data.schemas.ChangePasswordRequest
import com.suncar.suncartrabajador.data.schemas.LoginRequest
import com.suncar.suncartrabajador.data.schemas.LoginResponse
import com.suncar.suncartrabajador.data.services.AuthApiService
import com.suncar.suncartrabajador.domain.models.Brigada
import com.suncar.suncartrabajador.domain.models.TeamMember
import com.suncar.suncartrabajador.domain.models.User
import com.suncar.suncartrabajador.singleton.Auth
import java.io.IOException

class AuthService {
    private val authApiService: AuthApiService = RetrofitClient.createService()
    private val brigadaService = BrigadaService()

    suspend fun login(ci: String, contraseña: String): LoginResponse {
        return try {
            val request = LoginRequest(ci, contraseña)
            val response = authApiService.login(request)

            if (response.success) {
                val userInfo = response.user

                // Guardar token y datos básicos del usuario para futuras peticiones
                val token = response.token
                if (!token.isNullOrBlank() && userInfo != null && AuthPreferences.isInitialized()) {
                    AuthPreferences.saveAuth(
                            token = token,
                            ci = userInfo.ci,
                            nombre = userInfo.nombre,
                            rol = userInfo.rol
                    )
                }

                // Obtener la brigada completa desde el endpoint de brigadas
                val userCi = userInfo?.ci ?: ci
                var brigada: Brigada? = response.brigada // Usar brigada del login si existe (legacy)
                
                // Si no hay brigada en la respuesta del login, obtenerla del endpoint de brigadas
                if (brigada == null) {
                    val brigadaResult = brigadaService.getBrigadaByUserCi(userCi)
                    if (brigadaResult.isSuccess) {
                        brigada = brigadaResult.getOrNull()
                    }
                }

                // Si aún no hay brigada, crear una por defecto con el usuario como líder
                if (brigada == null) {
                    brigada = Brigada(
                        lider = TeamMember(
                            name = userInfo?.nombre ?: "Administrador",
                            id = userCi
                        ),
                        integrantes = emptyList()
                    )
                }

                // Crear el usuario con la información de la brigada
                val user = User(
                    ci = userCi,
                    name = userInfo?.nombre ?: brigada.lider.name,
                    password = contraseña,
                    brigada = brigada
                )
                Auth.setUser(user)
            }
            response
        } catch (e: IOException) {
            LoginResponse(
                    success = false,
                    message = "Error de conexión: Verifique su conexión a internet"
            )
        } catch (e: Exception) {
            LoginResponse(success = false, message = "Error inesperado: ${e.message}")
        }
    }

    suspend fun changePassword(ci: String, nuevaContrasena: String): Boolean {
        return try {
            val request = ChangePasswordRequest(ci, nuevaContrasena)
            val response = authApiService.changePassword(request)
            response.success
        } catch (e: IOException) {
            false
        } catch (e: Exception) {
            false
        }
    }
}
