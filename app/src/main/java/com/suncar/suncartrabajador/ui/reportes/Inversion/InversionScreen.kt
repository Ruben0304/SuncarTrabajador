package com.suncar.suncartrabajador.ui.reportes.Inversion

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.suncar.suncartrabajador.ui.features.Adjuntos.AdjuntosComposable
import com.suncar.suncartrabajador.ui.features.DateTime.DateTimeComposable
import com.suncar.suncartrabajador.ui.features.Materiales.MaterialesComposable
import com.suncar.suncartrabajador.ui.features.Ubicacion.UbicacionComposable
import com.suncar.suncartrabajador.ui.shared.HeaderSection
import com.suncar.suncartrabajador.ui.shared.SaveButton
import com.suncar.suncartrabajador.ui.shared.SendButton

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun InversionScreen(
    onBackPressed: () -> Unit = {},
    onSubmit: () -> Unit = {}
) {
    var isSubmitPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isSubmitPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "submit_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                HeaderSection("Reporte de Inversión", "Complete todos los campos requeridos para el reporte de inversión",modifier = Modifier.padding(bottom = 8.dp, top = 16.dp, start = 16.dp))
            }

            item { com.suncar.suncartrabajador.ui.features.Brigada.BrigadaComposable() }
            item { MaterialesComposable() }
            item { UbicacionComposable() }
            item { DateTimeComposable() }
            item { AdjuntosComposable() }
            item {
                SendButton(modifier = Modifier.scale(scale), onClick = {
                    isSubmitPressed = true
                    onSubmit()
                })
            }
            item {
                SaveButton(modifier = Modifier.scale(scale), onClick = {
                    isSubmitPressed = true
                    onSubmit()
                })
            }
        }

        // Botón de envío elegante en la parte inferior
//        Box(
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//
//        }

        // Reset pressed state
        LaunchedEffect(isSubmitPressed) {
            if (isSubmitPressed) {
                kotlinx.coroutines.delay(100)
                isSubmitPressed = false
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview
fun InversionScreenPreview() {
    MaterialTheme {
        InversionScreen()
    }
}