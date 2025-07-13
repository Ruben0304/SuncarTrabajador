# Servicio de Actualización de Aplicación

## Descripción

Este servicio implementa la funcionalidad de verificación y descarga de actualizaciones para la aplicación SuncarTrabajador. Se ejecuta automáticamente antes de cargar los datos iniciales de la aplicación.

## Características

- **Verificación automática**: Se ejecuta al iniciar la aplicación
- **Actualización opcional**: Permite al usuario decidir si actualizar o continuar
- **Actualización obligatoria**: Si `force_update` es `true`, no permite continuar sin actualizar
- **Descarga automática**: Descarga el APK y lo instala automáticamente
- **Manejo de errores**: Continúa con la carga normal si hay errores de conexión

## Flujo de Funcionamiento

1. **Inicio de la aplicación** → Se ejecuta `DatosInicialesComposable`
2. **Verificación de actualización** → Se llama al endpoint `/app/check-update`
3. **Respuesta del servidor**:
   - Si `is_up_to_date: true` → Continúa con la carga normal
   - Si `is_up_to_date: false` → Muestra el diálogo de actualización
4. **Diálogo de actualización**:
   - Si `force_update: false` → Botones "Actualizar" y "Más tarde"
   - Si `force_update: true` → Solo botón "Actualizar"
5. **Descarga e instalación** → Descarga el APK y lo instala automáticamente

## Estructura de Archivos

```
app/src/main/java/com/suncar/suncartrabajador/
├── domain/models/
│   ├── UpdateInfo.kt              # Modelo de respuesta del servidor
│   └── UpdateRequest.kt           # Modelo de solicitud
├── data/services/
│   └── UpdateApiService.kt        # Interfaz de API
├── app/service_implementations/
│   ├── UpdateService.kt           # Lógica de negocio
│   └── UpdateApiServices.kt       # Configuración de Retrofit
├── ui/features/UpdateCheck/
│   ├── UpdateCheckState.kt        # Estado de la UI
│   ├── UpdateCheckViewModel.kt    # ViewModel
│   ├── UpdateCheckViewModelFactory.kt # Fábrica del ViewModel
│   └── UpdateDialog.kt            # Diálogo de actualización
└── utils/
    └── AppUtils.kt                # Utilidades para obtener versión
```

## Configuración del Backend

El backend debe implementar el endpoint `POST /app/check-update` que recibe:

```json
{
  "current_version": "1.0.0",
  "platform": "android"
}
```

Y responde con:

```json
{
  "is_up_to_date": false,
  "latest_version": "1.2.0",
  "download_url": "https://example.com/app-v1.2.0.apk",
  "file_size": 15728640,
  "changelog": "Nuevas funcionalidades y correcciones de bugs",
  "force_update": false
}
```

## Permisos Requeridos

Los siguientes permisos ya están configurados en `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
```

## FileProvider

El FileProvider está configurado para permitir la instalación de APKs:

```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

## Uso

El servicio se integra automáticamente en `DatosInicialesComposable`. No requiere configuración adicional.

### Ejemplo de uso manual:

```kotlin
val updateService = UpdateService(
    updateApiService = UpdateApiServices().api,
    context = context
)

val updateInfo = updateService.checkForUpdates("1.0.0")
if (!updateInfo.isUpToDate) {
    // Mostrar diálogo de actualización
}
```

## Manejo de Errores

- **Error de conexión**: La aplicación continúa con la carga normal
- **Error de descarga**: Se muestra un mensaje de error al usuario
- **Error de instalación**: Se maneja graciosamente sin interrumpir la aplicación

## Personalización

Para personalizar el comportamiento:

1. **Modificar el diálogo**: Editar `UpdateDialog.kt`
2. **Cambiar la lógica de verificación**: Modificar `UpdateCheckViewModel.kt`
3. **Ajustar el manejo de errores**: Editar `UpdateService.kt`
4. **Cambiar la URL del endpoint**: Modificar `UpdateApiService.kt`
