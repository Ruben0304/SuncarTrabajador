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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// --- Colores personalizados de la marca Suncar ---

// Naranja principal del logo (del degradado del sol)
val SuncarPrimaryOrange = Color(0xFFD09752)

// Azul oscuro del texto "SUNCAR"
val SuncarDarkBlue = Color(0xFF0A2A5B)

// Naranja secundario del texto "Operaciones"
val SuncarTertiaryOrange = Color(0xFFF07F2D)

// Amarillo brillante de los rayos del sol
val SuncarAccentYellow = Color(0xFFFDD835)

// Colores neutros para texto y fondos
val SuncarTextDark = Color(0xFF0A2A5B) // Usando el azul oscuro para texto principal
val SuncarTextLight = Color(0xFFFFFFFF)
val SuncarBackgroundDark = Color(0xFF121212)
val SuncarBackgroundLight = Color(0xFFFFFFFF)
val SuncarSurfaceDark = Color(0xFF1E1E1E)
val SuncarSurfaceLight = Color(0xFFF1EFEF)


// --- Esquema para el Tema Claro ---

private val LightColorScheme = lightColorScheme(
    primary = SuncarPrimaryOrange,
    onPrimary = SuncarTextLight, // Texto blanco sobre naranja se ve bien
    secondary = SuncarDarkBlue,
    onSecondary = SuncarTextLight,
    tertiary = SuncarTertiaryOrange,
    onTertiary = SuncarTextLight,
    background = SuncarBackgroundLight,
    surface = SuncarSurfaceLight,
    onBackground = SuncarTextDark, // Azul oscuro para texto sobre fondo claro
    onSurface = SuncarTextDark
)


// --- Esquema para el Tema Oscuro ---

private val DarkColorScheme = darkColorScheme(
    primary = SuncarPrimaryOrange, // Amarillo para destacar en tema oscuro
    onPrimary = Black,  // Texto azul oscuro sobre amarillo
    secondary = SuncarDarkBlue,
    onSecondary = SuncarTextLight,
    tertiary = SuncarTertiaryOrange,
    onTertiary = SuncarTextLight,
    background = SuncarBackgroundDark,
    surface = SuncarSurfaceDark,
    onBackground = SuncarTextLight, // Texto blanco sobre fondo oscuro
    onSurface = SuncarTextLight
)
@Composable
fun SuncarTrabajadorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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