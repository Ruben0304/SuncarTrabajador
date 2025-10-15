# Deeplinks - Suncar Trabajador

## Descripción

La aplicación Suncar Trabajador ahora soporta deeplinks para abrir directamente la pantalla de creación de reportes con un cliente preseleccionado.

## Formato del Deeplink

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

### Crear reporte de Avería para cliente 12345
```
suncartrabajador://crear/averia/12345
```

### Crear reporte de Mantenimiento para cliente 67890
```
suncartrabajador://crear/mantenimiento/67890
```

### Crear reporte de Inversión para cliente 11111
```
suncartrabajador://crear/inversion/11111
```

## Cómo Usar desde WhatsApp u Otras Apps

1. **Desde WhatsApp**:
   - Envía el enlace completo en un mensaje
   - Al tocar el enlace, se abrirá la aplicación Suncar Trabajador directamente en la pantalla de crear reporte con el cliente preseleccionado

2. **Desde SMS**:
   - Similar a WhatsApp, el sistema detectará el enlace automáticamente

3. **Desde Navegador**:
   - Ingresa el enlace en la barra de direcciones
   - El navegador preguntará si deseas abrir la aplicación

4. **Desde cualquier app que soporte enlaces**:
   - El sistema operativo detectará el esquema `suncartrabajador://` y abrirá la aplicación automáticamente

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

- El deeplink usa el esquema `suncartrabajador://` con el host `crear`
- La implementación usa `android:launchMode="singleTask"` para evitar múltiples instancias de la app
- Los tipos de reporte no son case-sensitive (se convierten a minúsculas automáticamente)
- El número de cliente se valida automáticamente contra el servicio backend

## Ejemplo de Integración en HTML/Web

```html
<a href="suncartrabajador://crear/averia/12345">Crear Reporte de Avería</a>
```

## Ejemplo de Generación Dinámica en JavaScript

```javascript
function generarDeeplink(tipo, numeroCliente) {
  return `suncartrabajador://crear/${tipo}/${numeroCliente}`;
}

// Uso
const link = generarDeeplink('mantenimiento', '12345');
// Resultado: suncartrabajador://crear/mantenimiento/12345
```
