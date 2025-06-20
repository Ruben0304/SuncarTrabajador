package com.suncar.suncartrabajador.data.repositories

import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DateTimeRepository {

    suspend fun getInitialDateTimeData(): InitialDateTimeData {
        delay(500) // Simulate network delay
        val currentDate = LocalDate.now()
        val currentTime = LocalTime.now()
        
        // Hora de inicio por defecto: 1 hora antes de la hora actual
        val defaultStartTime = currentTime.minusHours(1)
        
        return InitialDateTimeData(
            currentDate = currentDate,
            startTime = defaultStartTime,
            endTime = currentTime
        )
    }

    fun getCurrentDate(): LocalDate {
        return LocalDate.now()
    }

    fun getCurrentTime(): LocalTime {
        return LocalTime.now()
    }
}

data class InitialDateTimeData(
    val currentDate: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime
) 