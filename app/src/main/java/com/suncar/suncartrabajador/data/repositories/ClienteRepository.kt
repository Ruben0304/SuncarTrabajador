package com.suncar.suncartrabajador.data.repositories

import com.suncar.suncartrabajador.domain.models.Cliente
import kotlinx.coroutines.delay

class ClienteRepository {
    private val clientes = listOf(
        Cliente("1001", "Empresa Alpha", "Calle 123, Ciudad"),
        Cliente("1002", "Comercial Beta", "Avenida 456, Ciudad"),
        Cliente("1003", "Servicios Gamma", "Boulevard 789, Ciudad"),
        Cliente("1004", "Industria Delta", "Ruta 101, Ciudad"),
        Cliente("1005", "Negocio Epsilon", "Camino 202, Ciudad")
    )

    suspend fun validarNumeroCliente(numero: String): Boolean {
        delay(300) // Simula validación asíncrona
        return clientes.any { it.numero == numero }
    }

    suspend fun obtenerClientePorNumero(numero: String): Cliente? {
        delay(300) // Simula consulta asíncrona
        return clientes.find { it.numero == numero }
    }

    fun obtenerNumerosClientes(): List<String> {
        return clientes.map { it.numero }
    }
} 