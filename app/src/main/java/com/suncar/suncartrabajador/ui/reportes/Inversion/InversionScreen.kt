package com.suncar.suncartrabajador.ui.reportes.Inversion


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.suncar.suncartrabajador.ui.features.Adjuntos.AdjuntosComposable
import com.suncar.suncartrabajador.ui.features.Materiales.MaterialesComposable
import com.suncar.suncartrabajador.ui.features.Brigada.BrigadaComposable
import com.suncar.suncartrabajador.ui.features.DateTime.DateTimeComposable
import com.suncar.suncartrabajador.ui.features.Ubicacion.UbicacionComposable

import com.suncar.suncartrabajador.ui.theme.SuncarTrabajadorTheme

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview
fun InversionScreen() {
    SuncarTrabajadorTheme {
        Box(modifier = Modifier.fillMaxSize().background(color = Color.Black)) {
            LazyColumn(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)) {
                item { UbicacionComposable() }
//                item { DateTimeComposable() }
//                item { AdjuntosComposable() }
//               item {  BrigadaComposable() }
//                item {  MaterialesComposable() }

            }
        }

    }
}