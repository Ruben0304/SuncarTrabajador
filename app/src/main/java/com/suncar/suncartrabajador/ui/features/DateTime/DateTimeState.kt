package com.suncar.suncartrabajador.ui.features.DateTime

import java.time.LocalDate
import java.time.LocalTime

data class DateTimeState(
    val currentDate: LocalDate? = null,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    val isLoading: Boolean = false
) 