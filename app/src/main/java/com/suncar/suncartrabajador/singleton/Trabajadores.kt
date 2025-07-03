package com.suncar.suncartrabajador.singleton

import com.suncar.suncartrabajador.domain.models.TeamMember
import com.suncar.suncartrabajador.domain.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton que maneja la información de trabajadores de manera global en la aplicación
 */
object Trabajadores {
    private val _trabajadoresDisponibles = MutableStateFlow<List<TeamMember>?>(null)
    val trabajadoresDisponibles: StateFlow<List<TeamMember>?> = _trabajadoresDisponibles.asStateFlow()

    /**
     * Establece la lista completa de trabajadores disponibles
     */
    fun setTrabajadores(trabajadores: List<TeamMember>) {
        _trabajadoresDisponibles.value = trabajadores
    }

    /**
     * Agrega un nuevo trabajador a la lista
     */
    fun addTrabajador(trabajador: TeamMember) {
        val currentList = _trabajadoresDisponibles.value?.toMutableList() ?: mutableListOf()
        currentList.add(trabajador)
        _trabajadoresDisponibles.value = currentList
    }

    /**
     * Actualiza un trabajador existente por su ID
     */
    fun updateTrabajador(trabajadorActualizado: TeamMember) {
        val currentList = _trabajadoresDisponibles.value?.toMutableList() ?: return
        val index = currentList.indexOfFirst { it.id == trabajadorActualizado.id }
        if (index != -1) {
            currentList[index] = trabajadorActualizado
            _trabajadoresDisponibles.value = currentList
        }
    }

    /**
     * Elimina un trabajador por su ID
     */
    fun removeTrabajador(trabajadorId: String) {
        val currentList = _trabajadoresDisponibles.value?.toMutableList() ?: return
        currentList.removeAll { it.id == trabajadorId }
        _trabajadoresDisponibles.value = currentList
    }

    /**
     * Obtiene un trabajador específico por su ID
     */
    fun getTrabajadorById(trabajadorId: String): TeamMember? {
        return _trabajadoresDisponibles.value?.find { it.id == trabajadorId }
    }

    /**
     * Obtiene la lista completa de trabajadores disponibles
     */
    fun getTrabajadores(): List<TeamMember> {
        return _trabajadoresDisponibles.value ?: emptyList()
    }



    /**
     * Verifica si hay trabajadores cargados
     */
    fun hasTrabajadores(): Boolean {
        return _trabajadoresDisponibles.value?.isNotEmpty() == true
    }

    /**
     * Obtiene el número total de trabajadores
     */
    fun getTotalTrabajadores(): Int {
        return _trabajadoresDisponibles.value?.size ?: 0
    }

    /**
     * Limpia la lista de trabajadores (logout/clear)
     */
    fun clear() {
        _trabajadoresDisponibles.value = null
    }

    /**
     * Verifica si un trabajador existe por su ID
     */
    fun existsTrabajador(trabajadorId: String): Boolean {
        return _trabajadoresDisponibles.value?.any { it.id == trabajadorId } == true
    }
}