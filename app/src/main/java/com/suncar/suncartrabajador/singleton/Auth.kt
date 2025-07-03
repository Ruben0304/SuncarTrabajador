package com.suncar.suncartrabajador.singleton

import com.suncar.suncartrabajador.domain.models.Brigada
import com.suncar.suncartrabajador.domain.models.TeamMember
import com.suncar.suncartrabajador.domain.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton que maneja la información de la brigada de manera global en la aplicación
 */
object Auth {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()


    fun setUser(user: User) {
        _currentUser.value = user
    }

    /**
     * Limpia la información de brigada y usuario (logout)
     */
    fun clear() {
        _currentUser.value = null
    }

    /**
     * Obtiene la brigada actual (nullable)
     */
    fun getCurrentBrigada(): Brigada? = _currentUser.value?.brigada

    /**
     * Obtiene el usuario actual (nullable)
     */
    fun getCurrentUser(): User? = _currentUser.value

    /**
     * Obtiene la lista de integrantes de la brigada actual
     */
    fun getIntegrantes(): List<TeamMember> {
        return getCurrentBrigada()?.integrantes ?: emptyList()
    }

    /**
     * Verifica si hay un usuario autenticado
     */
    fun isUserAuthenticated(): Boolean {
        return _currentUser.value != null
    }

    /**
     * Cierra la sesión del usuario actual
     */
    fun logout() {
        clear()
    }
}