package com.suncar.suncartrabajador.ui.screens.Nuevo

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suncar.suncartrabajador.ui.shared.HeaderSection

@Composable
fun NuevoComposable(
    modifier: Modifier = Modifier,
    onNavigateToInversion: () -> Unit = {},
    onNavigateToOperacion: () -> Unit = {},
    onNavigateToAveria: () -> Unit = {},
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header section
        HeaderSection("¿Qué deseas reportar?", "Selecciona el tipo de reporte que necesitas crear")

        Spacer(modifier = Modifier.height(40.dp))

        // Cards section
        CardsSection(
            onNavigateToInversion = onNavigateToInversion,
            onNavigateToOperacion = onNavigateToOperacion,
            onNavigateToAveria = onNavigateToAveria
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}


@Composable
private fun CardsSection(
    onNavigateToInversion: () -> Unit,
    onNavigateToOperacion: () -> Unit,
    onNavigateToAveria: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Avería
        EnhancedOptionCard(
            icon = Icons.Default.Warning,
            title = "Avería o Queja",
            description = "",
            gradientColors = listOf(
                Color(0xFFFF6B6B).copy(alpha = 0.1f),
                Color(0xFFFF8E8E).copy(alpha = 0.05f)
            ),
            iconColor = Color(0xFFFF6B6B),
            onClick = onNavigateToAveria
        )

        // Operación
        EnhancedOptionCard(
            icon = Icons.Default.Assessment,
            title = "Mantenimiento",
            description = "",
            gradientColors = listOf(
                Color(0xFF4ECDC4).copy(alpha = 0.1f),
                Color(0xFF7FDBDA).copy(alpha = 0.05f)
            ),
            iconColor = Color(0xFF4ECDC4),
            onClick = onNavigateToOperacion
        )

        // Inversión
        EnhancedOptionCard(
            icon = Icons.Default.Build,
            title = "Inversión",
            description = "",
            gradientColors = listOf(
                Color(0xFF45B7D1).copy(alpha = 0.1f),
                Color(0xFF96CDF0).copy(alpha = 0.05f)
            ),
            iconColor = Color(0xFF45B7D1),
            onClick = onNavigateToInversion
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedOptionCard(
    icon: ImageVector,
    title: String,
    description: String,
    gradientColors: List<Color>,
    iconColor: Color,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "card_scale"
    )

    Card(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .scale(scale),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 12.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Fondo degradado sutil
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(gradientColors),
                        RoundedCornerShape(20.dp)
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Contenedor del ícono con fondo circular
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            iconColor.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        modifier = Modifier.size(32.dp),
                        tint = iconColor
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Indicador visual de navegación
                Icon(
                    imageVector = Icons.Default.Assessment, // Puedes usar ChevronRight si está disponible
                    contentDescription = "Ir a $title",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }

    // Reset pressed state
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}