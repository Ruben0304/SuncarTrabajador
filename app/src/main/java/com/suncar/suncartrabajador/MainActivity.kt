package com.suncar.suncartrabajador

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.suncar.suncartrabajador.ui.screens.Login.LoginComposable
import com.suncar.suncartrabajador.ui.layout.MainAppContent
import com.suncar.suncartrabajador.ui.theme.SuncarTrabajadorTheme
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.suncar.suncartrabajador.app.service_implementations.AuthService
import com.suncar.suncartrabajador.ui.shared.AdvancedLottieAnimation
import com.suncar.suncartrabajador.ui.features.DatosIniciales.DatosInicialesComposable
import com.suncar.suncartrabajador.singleton.Auth
import com.suncar.suncartrabajador.data.local.SessionManager
import com.suncar.suncartrabajador.domain.models.User
import com.suncar.suncartrabajador.ui.navigation.AppNavigationViewModel
import com.suncar.suncartrabajador.ui.navigation.AppScreen

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuncarTrabajadorTheme {
                val context = LocalContext.current
                val window = (context as Activity).window

                LaunchedEffect(Unit) {
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                        controller.hide(WindowInsetsCompat.Type.navigationBars())
                        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    }
                }
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
    val navigationViewModel: AppNavigationViewModel = viewModel()
    val currentScreen by navigationViewModel.currentScreen.collectAsState()

    LaunchedEffect(Unit) {
        navigationViewModel.navigateTo(AppScreen.LOADING_DATA)
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            slideInHorizontally(
                animationSpec = tween(300),
                initialOffsetX = { fullWidth -> fullWidth }
            ) + fadeIn(
                animationSpec = tween(300)
            ) togetherWith slideOutHorizontally(
                animationSpec = tween(300),
                targetOffsetX = { fullWidth -> -fullWidth }
            ) + fadeOut(
                animationSpec = tween(300)
            )
        }
    ) { screen ->
        when (screen) {
            AppScreen.LOADING_DATA -> {
                DatosInicialesComposable(
                    modifier = Modifier.fillMaxSize(),
                    onDataLoadComplete = {
                        if (Auth.isUserAuthenticated())
                            navigationViewModel.navigateTo(AppScreen.MAIN_APP)
                        else
                            navigationViewModel.navigateTo((AppScreen.LOGIN))
                    }
                )
            }
            AppScreen.LOGIN -> {
                LoginComposable(
                    modifier = Modifier.fillMaxSize(),
                    onLoginSuccess = {
                        navigationViewModel.navigateTo(AppScreen.LOADING_DATA)
                    }
                )
            }
            AppScreen.MAIN_APP -> {
                MainAppContent(
                    onLogout = {
                        navigationViewModel.logout()
                    }
                )
            }
        }
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