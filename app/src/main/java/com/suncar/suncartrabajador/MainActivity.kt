package com.suncar.suncartrabajador

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.LocationSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.suncar.suncartrabajador.ui.screens.Login.LoginComposable
import com.suncar.suncartrabajador.ui.layout.MainAppContent
import com.suncar.suncartrabajador.ui.theme.SuncarTrabajadorTheme
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.suncar.suncartrabajador.ui.shared.AdvancedLottieAnimation

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuncarTrabajadorTheme {
//                GoogleMap(modifier = Modifier.fillMaxSize() ) {
//
//                }
                SuncarTrabajadorApp()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun SuncarTrabajadorApp() {
    var isLoading by remember { mutableStateOf(true) }
    var isLoggedIn by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000)
        isLoading = false
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AdvancedLottieAnimation()

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Cargando datos...",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }else if (!isLoggedIn) {
        // Mostrar pantalla de login
        LoginComposable(
            modifier = Modifier.fillMaxSize()
        )
        // TODO: Implementar lógica para detectar login exitoso
        // Por ahora, simulamos que el usuario está logueado después de un tiempo
        LaunchedEffect(Unit) {
            // Simular login automático después de 3 segundos para testing
            kotlinx.coroutines.delay(3000)
            isLoggedIn = true
        }
    } else {
        // Mostrar aplicación principal
        MainAppContent()
    }
}


@RequiresApi(Build.VERSION_CODES.S)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SuncarTrabajadorTheme {
        SuncarTrabajadorApp()
    }
}