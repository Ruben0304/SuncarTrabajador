package com.suncar.suncartrabajador.ui.features.FirmaCliente

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suncar.suncartrabajador.ui.components.CustomCard
import java.io.File

@Composable
fun FirmaClienteComposable(
        modifier: Modifier = Modifier,
        firmaClienteViewModel: FirmaClienteViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by firmaClienteViewModel.state.collectAsState()
    var signaturePadSize by remember { mutableStateOf(IntSize.Zero) }
    val penWidth = 3.dp
    val penColor = MaterialTheme.colorScheme.onSurface
    val penWidthPx = with(LocalDensity.current) { penWidth.toPx() }

    // Estado para almacenar los trazos de la firma
    var paths by remember { mutableStateOf(listOf<List<Offset>>()) }
    var currentPath by remember { mutableStateOf(listOf<Offset>()) }

    // Función para guardar el bitmap en un archivo temporal y obtener su Uri
    fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri? {
        return try {
            val imagesDir = File(context.cacheDir, "images")
            if (!imagesDir.exists()) imagesDir.mkdirs()
            val file = File.createTempFile("firma_cliente_", ".png", imagesDir)
            file.outputStream().use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
            val authority =
                    context.packageName +
                            ".fileprovider" // <--- Cambiado de .provider a .fileprovider
            FileProvider.getUriForFile(context, authority, file)
        } catch (e: Exception) {
            null
        }
    }

    CustomCard(
            modifier = modifier.padding(16.dp),
            title = "Firma del cliente",
            subtitle = "Por favor, firme para confirmar la recepción del servicio.",
            icon = Icons.Filled.Edit,
            isLoading = false
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (state.signatureUri == null) {
                Box(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .height(250.dp)
                                        .border(
                                                BorderStroke(
                                                        1.dp,
                                                        MaterialTheme.colorScheme.outline
                                                ),
                                                MaterialTheme.shapes.medium
                                        )
                                        .onSizeChanged { size -> signaturePadSize = size }
                                        .pointerInput(Unit) {
                                            detectDragGestures(
                                                    onDragStart = { offset ->
                                                        currentPath = listOf(offset)
                                                    },
                                                    onDrag = { change, _ ->
                                                        currentPath = currentPath + change.position
                                                    },
                                                    onDragEnd = {
                                                        if (currentPath.isNotEmpty()) {
                                                            paths = paths + listOf(currentPath)
                                                            currentPath = listOf()
                                                        }
                                                    },
                                                    onDragCancel = { currentPath = listOf() }
                                            )
                                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Dibujar los paths existentes
                        for (path in paths) {
                            for (i in 1 until path.size) {
                                drawLine(
                                        color = penColor,
                                        start = path[i - 1],
                                        end = path[i],
                                        strokeWidth = penWidthPx
                                )
                            }
                        }
                        // Dibujar el path actual
                        for (i in 1 until currentPath.size) {
                            drawLine(
                                    color = penColor,
                                    start = currentPath[i - 1],
                                    end = currentPath[i],
                                    strokeWidth = penWidthPx
                            )
                        }
                    }
                }
                Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                            onClick = {
                                paths = listOf()
                                currentPath = listOf()
                                firmaClienteViewModel.clearSignature()
                            }
                    ) { Text("Limpiar") }

                    Button(
                            onClick = {
                                if (paths.isEmpty() && currentPath.isEmpty()) {
                                    firmaClienteViewModel.onError("Debe firmar antes de continuar")
                                } else if (signaturePadSize == IntSize.Zero) {
                                    firmaClienteViewModel.onError(
                                            "Error al obtener el área de la firma. Intente de nuevo."
                                    )
                                } else {
                                    // Crear un ImageBitmap y dibujar los paths en él
                                    val imageBitmap =
                                            ImageBitmap(
                                                    signaturePadSize.width,
                                                    signaturePadSize.height
                                            )
                                    val canvas =
                                            android.graphics.Canvas(imageBitmap.asAndroidBitmap())
                                    val paint =
                                            android.graphics.Paint().apply {
                                                color = penColor.toArgb()
                                                strokeWidth = penWidthPx
                                                style = android.graphics.Paint.Style.STROKE
                                                isAntiAlias = true
                                                strokeCap = android.graphics.Paint.Cap.ROUND
                                            }
                                    // Dibujar los paths
                                    for (path in paths) {
                                        for (i in 1 until path.size) {
                                            canvas.drawLine(
                                                    path[i - 1].x,
                                                    path[i - 1].y,
                                                    path[i].x,
                                                    path[i].y,
                                                    paint
                                            )
                                        }
                                    }
                                    // Dibujar el path actual
                                    for (i in 1 until currentPath.size) {
                                        canvas.drawLine(
                                                currentPath[i - 1].x,
                                                currentPath[i - 1].y,
                                                currentPath[i].x,
                                                currentPath[i].y,
                                                paint
                                        )
                                    }
                                    val androidBitmap = imageBitmap.asAndroidBitmap()
                                    val uri = saveBitmapToCache(context, androidBitmap)
                                    if (uri != null) {
                                        firmaClienteViewModel.setSignature(uri)
                                        // Limpiar paths después de guardar
                                        paths = listOf()
                                        currentPath = listOf()
                                    } else {
                                        firmaClienteViewModel.onError(
                                                "No se pudo guardar la firma. Intente de nuevo."
                                        )
                                    }
                                }
                            }
                    ) { Text("Confirmar firma") }
                }
            } else {
                // Mostrar la firma como imagen
                val imageBitmap =
                        remember(state.signatureUri) {
                            state.signatureUri?.let {
                                try {
                                    val input = context.contentResolver.openInputStream(it)
                                    val bmp = android.graphics.BitmapFactory.decodeStream(input)
                                    input?.close()
                                    bmp?.asImageBitmap()
                                } catch (e: Exception) {
                                    null
                                }
                            }
                        }
                if (imageBitmap != null) {
                    Image(
                            bitmap = imageBitmap,
                            contentDescription = "Firma del cliente",
                            modifier =
                                    Modifier.fillMaxWidth()
                                            .height(250.dp)
                                            .border(
                                                    BorderStroke(
                                                            1.dp,
                                                            MaterialTheme.colorScheme.outline
                                                    ),
                                                    MaterialTheme.shapes.medium
                                            )
                    )
                }
                Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                            onClick = {
                                firmaClienteViewModel.clearSignature()
                                paths = listOf()
                                currentPath = listOf()
                            }
                    ) { Text("Rehacer firma") }
                }
            }
            if (state.error != null) {
                Text(
                        text = state.error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
