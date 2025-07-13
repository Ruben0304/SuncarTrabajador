package com.suncar.suncartrabajador.ui.features.Descripcion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suncar.suncartrabajador.ui.components.CustomCard

@Composable
fun DescripcionComposable(
    modifier: Modifier = Modifier,
    descripcionViewModel: DescripcionViewModel = viewModel()
) {
    val state by descripcionViewModel.state.collectAsState()

    CustomCard(
        modifier = modifier.padding(16.dp),
        title = "Descripción",
        subtitle = "Describa la situación",
        icon = Icons.Filled.Description,
        isLoading = false
    ) {
        OutlinedTextField(
            value = state.descripcion,
            onValueChange = { descripcionViewModel.onDescripcionChanged(it) },
            label = { Text("Descripción") },
            placeholder = { Text("Describa la situación") },
            isError = state.error != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default
            ),
            singleLine = false,
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )
        if (state.error != null) {
            Text(
                text = state.error ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
} 