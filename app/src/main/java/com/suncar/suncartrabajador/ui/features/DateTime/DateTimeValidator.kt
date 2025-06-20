package com.suncar.suncartrabajador.ui.features.DateTime

import java.time.LocalTime

class DateTimeValidator {
    fun isValid(state: DateTimeState): Boolean {
        return state.startTime != null && 
               state.endTime != null && 
               state.currentDate != null &&
               state.startTime.isBefore(state.endTime)
    }
} 