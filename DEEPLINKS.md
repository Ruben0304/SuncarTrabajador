# Deeplinks y App Links - Suncar Trabajador

## Descripción

La aplicación Suncar Trabajador soporta dos tipos de enlaces para abrir directamente la pantalla de creación de reportes con un cliente preseleccionado:

1. **Deep Links** (esquema personalizado): Para testing con ADB
2. **App Links** (HTTPS): Para uso real desde WhatsApp, SMS, navegadores, etc.

## Formato de Enlaces

### App Links (HTTPS) - **RECOMENDADO para WhatsApp**

```
https://api.suncarsrl.com/app/crear/{tipo}/{numeroCliente}
```

### Deep Links (Esquema personalizado) - Para testing

```
suncartrabajador://crear/{tipo}/{numeroCliente}
```

### Parámetros

- **tipo**: El tipo de reporte a crear. Valores válidos:
  - `averia` - Reporte de Avería
  - `mantenimiento` - Reporte de Mantenimiento
  - `inversion` - Reporte de Inversión

- **numeroCliente**: El número del cliente que se preseleccionará automáticamente

## Ejemplos de Uso

### App Links (HTTPS) - Para WhatsApp y otras apps

#### Crear reporte de Avería para cliente 12345
```
https://api.suncarsrl.com/app/crear/averia/12345
```

#### Crear reporte de Mantenimiento para cliente 67890
```
https://api.suncarsrl.com/app/crear/mantenimiento/67890
```

#### Crear reporte de Inversión para cliente 11111
```
https://api.suncarsrl.com/app/crear/inversion/11111
```

### Deep Links - Para testing con ADB

#### Crear reporte de Avería para cliente 12345
```bash
adb shell am start -a android.intent.action.VIEW -d "suncartrabajador://crear/averia/12345"
```

#### Crear reporte de Mantenimiento para cliente 67890
```bash
adb shell am start -a android.intent.action.VIEW -d "suncartrabajador://crear/mantenimiento/67890"
```

#### Crear reporte de Inversión para cliente 11111
```bash
adb shell am start -a android.intent.action.VIEW -d "suncartrabajador://crear/inversion/11111"
```

## Cómo Usar desde WhatsApp u Otras Apps

### Con App Links (HTTPS)

1. **Desde WhatsApp**:
   - Envía el enlace HTTPS en un mensaje: `https://api.suncarsrl.com/app/crear/averia/12345`
   - WhatsApp lo reconocerá como enlace clickeable (azul y subrayado)
   - Al tocar el enlace, se abrirá automáticamente la aplicación Suncar Trabajador

2. **Desde SMS**:
   - Envía el enlace HTTPS: `https://api.suncarsrl.com/app/crear/mantenimiento/67890`
   - El sistema lo detectará como enlace automáticamente

3. **Desde Navegador**:
   - Ingresa la URL HTTPS en la barra de direcciones
   - Android preguntará si deseas abrir con la aplicación Suncar Trabajador

4. **Desde Email, Slack, Teams, etc.**:
   - Cualquier app que soporte enlaces HTTPS funcionará automáticamente

## Comportamiento

1. Al abrir el deeplink, la aplicación:
   - Verifica que el usuario esté autenticado
   - Navega directamente a la pantalla de creación del tipo de reporte especificado
   - Precarga automáticamente la información del cliente usando el número proporcionado
   - Valida el cliente con el servicio backend

2. Si el cliente no existe:
   - Se mostrará un mensaje de error indicando que el cliente no fue encontrado
   - El usuario puede ingresar otro número de cliente manualmente

3. Si el usuario no está autenticado:
   - La aplicación mostrará la pantalla de login primero
   - Después del login exitoso, el deeplink será procesado

## Notas Técnicas

- **App Links (HTTPS)**: Usa el dominio `api.suncarsrl.com` con el path `/app/crear`
- **Deep Links**: Usa el esquema `suncartrabajador://` con el host `crear`
- La implementación usa `android:launchMode="singleTask"` para evitar múltiples instancias de la app
- Los tipos de reporte no son case-sensitive (se convierten a minúsculas automáticamente)
- El número de cliente se valida automáticamente contra el servicio backend
- Android verifica automáticamente la propiedad del dominio mediante el archivo `assetlinks.json`

## 🔧 Configuración del Backend

Para que los App Links funcionen, necesitas configurar tu backend FastAPI.

**Ver instrucciones completas en:** [BACKEND_SETUP.md](BACKEND_SETUP.md)

### Resumen rápido:

1. Servir el archivo `assetlinks.json` en:
   ```
   https://api.suncarsrl.com/.well-known/assetlinks.json
   ```

2. (Opcional) Crear endpoint que responda a:
   ```
   https://api.suncarsrl.com/app/crear/{tipo}/{numeroCliente}
   ```

## Ejemplo de Integración en HTML/Web

### App Links (Para WhatsApp, Email, etc.)
```html
<a href="https://api.suncarsrl.com/app/crear/averia/12345">Crear Reporte de Avería</a>
```

### Deep Links (Solo para testing)
```html
<a href="suncartrabajador://crear/averia/12345">Crear Reporte de Avería</a>
```

## Ejemplo de Generación Dinámica en JavaScript

### Para App Links (HTTPS)
```javascript
function generarAppLink(tipo, numeroCliente) {
  return `https://api.suncarsrl.com/app/crear/${tipo}/${numeroCliente}`;
}

// Uso
const link = generarAppLink('mantenimiento', '12345');
// Resultado: https://api.suncarsrl.com/app/crear/mantenimiento/12345
```

### Para Deep Links (Testing)
```javascript
function generarDeeplink(tipo, numeroCliente) {
  return `suncartrabajador://crear/${tipo}/${numeroCliente}`;
}

// Uso
const link = generarDeeplink('mantenimiento', '12345');
// Resultado: suncartrabajador://crear/mantenimiento/12345
```

## 📱 Ejemplo de Uso desde Python/Backend

Si quieres generar y enviar estos links desde tu backend FastAPI:

```python
def generar_link_reporte(tipo: str, numero_cliente: str) -> str:
    """
    Genera un App Link para crear un reporte

    Args:
        tipo: 'averia', 'mantenimiento' o 'inversion'
        numero_cliente: Número del cliente

    Returns:
        URL completa del App Link
    """
    return f"https://api.suncarsrl.com/app/crear/{tipo}/{numero_cliente}"

# Ejemplo de uso en WhatsApp Business API o envío de notificaciones
link = generar_link_reporte('averia', '12345')
mensaje = f"Hola, por favor completa el reporte: {link}"
```
