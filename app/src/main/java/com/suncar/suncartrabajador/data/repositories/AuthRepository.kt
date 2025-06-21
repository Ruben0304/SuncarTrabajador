package com.suncar.suncartrabajador.data.repositories

import kotlinx.coroutines.delay

class AuthRepository {
    
    // Simulación de base de datos de usuarios
    private val validUsers = mapOf(
        "12345678" to "password123",
        "87654321" to "password456",
        "11223344" to "password789"
    )
    
    suspend fun login(ci: String, password: String): LoginResult {
        delay(1500) // Simular delay de red
        
        return when {
            validUsers[ci] == password -> LoginResult.Success(User(ci, "Usuario $ci"))
            else -> LoginResult.Error("CI o contraseña incorrectos")
        }
    }
}

sealed class LoginResult {
    data class Success(val user: User) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

data class User(
    val ci: String,
    val name: String
) 