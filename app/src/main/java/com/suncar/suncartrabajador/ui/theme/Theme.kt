package com.suncar.suncartrabajador.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


private val DarkColorScheme = darkColorScheme(
    primary = Orange200, // Lighter orange for dark theme primary
    onPrimary = Black, // Text on primary background should be dark
    secondary = Teal200, // Lighter teal for secondary in dark theme
    tertiary = Orange700, // Darker orange as tertiary
    background = Grey800, // Dark grey background
    surface = Grey800, // Dark grey surface
    onBackground = White, // White text on dark background
    onSurface = White // White text on dark surface
)

private val LightColorScheme = lightColorScheme(
    primary = Orange500, // Elegant orange as primary
    onPrimary = White, // White text on primary background
    secondary = Teal700, // Darker teal as secondary
    onSecondary = White, // White text on secondary background
    tertiary = Orange700, // Darker orange as tertiary
    onTertiary = White, // White text on tertiary background
    background = White, // White background
    surface = White, // White surface
    onBackground = Grey800, // Dark grey text on white background
    onSurface = Grey800 // Dark grey text on white surface
)

@Composable
fun SuncarTrabajadorTheme(
    darkTheme: Boolean = true,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // You might also want to define your Typography if you haven't already
    // For example:
    // val typography = MaterialTheme.typography

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Assuming Typography is defined elsewhere in your theme package
        content = content
    )
}