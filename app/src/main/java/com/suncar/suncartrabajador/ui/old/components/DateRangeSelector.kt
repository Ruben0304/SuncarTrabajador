package com.suncar.suncartrabajador.ui.old.components

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun DateRangeSelector(
    startDate: LocalDate,
    endDate: LocalDate,
    onStartDateSelected: (LocalDate) -> Unit,
    onEndDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // DatePickerDialog para la fecha de inicio
    val startDatePickerDialog = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                onStartDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
            },
            startDate.year,
            startDate.monthValue - 1,
            startDate.dayOfMonth
        )
    }

    // DatePickerDialog para la fecha de fin
    val endDatePickerDialog = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                onEndDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
            },
            endDate.year,
            endDate.monthValue - 1,
            endDate.dayOfMonth
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Selector de Fecha de Inicio
        OutlinedTextField(
            value = startDate.format(dateFormatter),
            onValueChange = { /* No se permite la edición directa */ },
            label = { Text("Fecha de Inicio") },
            readOnly = true,
            trailingIcon = {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = "Seleccionar fecha de inicio",
                    modifier = Modifier.clickable { startDatePickerDialog.show() }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Selector de Fecha de Fin
        OutlinedTextField(
            value = endDate.format(dateFormatter),
            onValueChange = { /* No se permite la edición directa */ },
            label = { Text("Fecha de Fin") },
            readOnly = true,
            trailingIcon = {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = "Seleccionar fecha de fin",
                    modifier = Modifier.clickable { endDatePickerDialog.show() }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}