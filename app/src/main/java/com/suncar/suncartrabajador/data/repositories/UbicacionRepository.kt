package com.suncar.suncartrabajador.data.repositories

import com.suncar.suncartrabajador.domain.models.LocationData
import kotlinx.coroutines.delay

class UbicacionRepository {

    suspend fun getInitialUbicacionData(): LocationData {
        delay(1000) // Simulate network delay
        // Simular obtención de GPS (en una implementación real usarías LocationManager)
        val defaultLatitude = -12.0464 // Lima, Perú
        val defaultLongitude = -77.0428
        val defaultAddress = "Lima, Perú"
        
        return LocationData(
            address = defaultAddress,
            coordinates = "$defaultLatitude,$defaultLongitude",
            distance = "0.0"
        )
    }

    suspend fun getCurrentLocation(): LocationData {
        delay(500) // Simulate GPS delay
        // En una implementación real, aquí obtendrías la ubicación actual via GPS
        val currentLatitude = -12.0464 + (Math.random() - 0.5) * 0.01 // Pequeña variación
        val currentLongitude = -77.0428 + (Math.random() - 0.5) * 0.01
        val currentAddress = ""
        
        return LocationData(
            address = currentAddress,
            coordinates = "$currentLatitude,$currentLongitude",
            distance = "0.0"
        )
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        // Fórmula de Haversine para calcular distancia entre dos puntos
        val r = 6371 // Radio de la Tierra en km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }
} 