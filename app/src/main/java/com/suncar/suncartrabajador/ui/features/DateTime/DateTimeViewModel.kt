package com.suncar.suncartrabajador.ui.features.DateTime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suncar.suncartrabajador.data.repositories.DateTimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class DateTimeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DateTimeState())
    val uiState: StateFlow<DateTimeState> = _uiState.asStateFlow()
    private val repository = DateTimeRepository()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val initialData = repository.getInitialDateTimeData()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    currentDate = initialData.currentDate,
                    startTime = initialData.startTime,
                    endTime = initialData.endTime
                )
            }
        }
    }

    fun updateDate(year: Int, month: Int, dayOfMonth: Int) {
        val newDate = LocalDate.of(year, month, dayOfMonth)
        _uiState.update { it.copy(currentDate = newDate) }
    }

    fun updateStartTime(hour: Int, minute: Int) {
        val newStartTime = LocalTime.of(hour, minute)
        _uiState.update { it.copy(startTime = newStartTime) }
    }

    fun updateEndTime(hour: Int, minute: Int) {
        val newEndTime = LocalTime.of(hour, minute)
        _uiState.update { it.copy(endTime = newEndTime) }
    }

    fun refreshCurrentTime() {
        val currentTime = repository.getCurrentTime()
        _uiState.update { it.copy(endTime = currentTime) }
    }
} 