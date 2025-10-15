# Configuración del Backend FastAPI para App Links

## 📋 Paso 1: Servir el archivo assetlinks.json

Para que Android verifique que tu app es dueña del dominio `api.suncarsrl.com`, necesitas servir el archivo `assetlinks.json` en una ruta específica.

### Ubicación requerida:
```
https://api.suncarsrl.com/.well-known/assetlinks.json
```

## 🔧 Implementación en FastAPI

### Opción A: Servir el archivo directamente (Recomendado)

1. **Copia el archivo `assetlinks.json` a tu proyecto FastAPI**

   Crea una carpeta `.well-known` en la raíz de tu proyecto FastAPI y copia el archivo:
   ```
   tu-proyecto-fastapi/
   ├── .well-known/
   │   └── assetlinks.json
   ├── main.py
   └── ...
   ```

2. **Agrega esta ruta en tu FastAPI** (`main.py` o donde tengas tus rutas):

```python
from fastapi import FastAPI
from fastapi.responses import FileResponse
import os

app = FastAPI()

@app.get("/.well-known/assetlinks.json")
async def get_assetlinks():
    """
    Endpoint para verificación de App Links de Android
    """
    file_path = os.path.join(os.path.dirname(__file__), ".well-known", "assetlinks.json")
    return FileResponse(
        file_path,
        media_type="application/json",
        headers={
            "Content-Type": "application/json",
            "Access-Control-Allow-Origin": "*"
        }
    )
```

### Opción B: Responder directamente con JSON

```python
from fastapi import FastAPI
from fastapi.responses import JSONResponse

app = FastAPI()

@app.get("/.well-known/assetlinks.json")
async def get_assetlinks():
    """
    Endpoint para verificación de App Links de Android
    """
    assetlinks = [
        {
            "relation": ["delegate_permission/common.handle_all_urls"],
            "target": {
                "namespace": "android_app",
                "package_name": "com.suncar.suncartrabajador",
                "sha256_cert_fingerprints": [
                    "04:9E:C7:2F:93:A4:8E:C5:AF:05:71:B7:AC:66:96:BC:83:D8:7B:18:C9:9E:5A:77:FC:15:31:EE:05:96:80:AD"
                ]
            }
        }
    ]

    return JSONResponse(
        content=assetlinks,
        headers={
            "Content-Type": "application/json",
            "Access-Control-Allow-Origin": "*"
        }
    )
```

## 📱 Paso 2: Crear endpoint para redireccionar (Opcional pero recomendado)

Este endpoint es opcional, pero mejora la experiencia si alguien abre el link desde una PC:

```python
from fastapi import FastAPI
from fastapi.responses import HTMLResponse

@app.get("/app/crear/{tipo}/{numero_cliente}")
async def redirect_to_app(tipo: str, numero_cliente: str):
    """
    Endpoint que maneja los App Links
    Si se abre desde Android con la app instalada, abre la app
    Si se abre desde PC o sin la app, muestra instrucciones
    """

    # HTML que se muestra si se abre desde navegador
    html_content = f"""
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Abrir en App Suncar Trabajador</title>
        <style>
            body {{
                font-family: Arial, sans-serif;
                max-width: 600px;
                margin: 50px auto;
                padding: 20px;
                text-align: center;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                min-height: 100vh;
            }}
            .container {{
                background: white;
                border-radius: 15px;
                padding: 30px;
                box-shadow: 0 10px 40px rgba(0,0,0,0.3);
            }}
            h1 {{ color: #333; }}
            .info {{
                background: #f5f5f5;
                padding: 20px;
                border-radius: 10px;
                margin: 20px 0;
            }}
            .button {{
                display: inline-block;
                padding: 15px 30px;
                background: #667eea;
                color: white;
                text-decoration: none;
                border-radius: 8px;
                margin: 10px;
                font-weight: bold;
            }}
        </style>
    </head>
    <body>
        <div class="container">
            <h1>🚀 Suncar Trabajador</h1>
            <div class="info">
                <h2>Crear Reporte de {tipo.capitalize()}</h2>
                <p><strong>Cliente:</strong> {numero_cliente}</p>
            </div>
            <p>Este enlace debe abrirse desde un dispositivo Android con la aplicación Suncar Trabajador instalada.</p>
            <p>Si tienes la app instalada, toca el botón de abajo:</p>
            <a href="suncartrabajador://crear/{tipo}/{numero_cliente}" class="button">
                Abrir en la App
            </a>
        </div>
    </body>
    </html>
    """

    return HTMLResponse(content=html_content)
```

## ✅ Verificación

Después de implementar estos cambios:

1. **Verifica que el archivo esté accesible**:
   ```bash
   curl https://api.suncarsrl.com/.well-known/assetlinks.json
   ```

   Debe devolver el JSON con la configuración.

2. **Verifica con la herramienta de Google**:
   - Ve a: https://developers.google.com/digital-asset-links/tools/generator
   - Ingresa tu dominio: `api.suncarsrl.com`
   - Ingresa el package: `com.suncar.suncartrabajador`
   - Verifica que la configuración sea correcta

## 🔐 IMPORTANTE: Para Producción

Cuando vayas a producción con un APK firmado (no debug), necesitarás:

1. Obtener la SHA256 de tu keystore de producción:
   ```bash
   keytool -list -v -keystore tu-keystore-de-produccion.jks -alias tu-alias
   ```

2. Agregar esa SHA256 al array de `sha256_cert_fingerprints` en el archivo `assetlinks.json`:
   ```json
   {
     "relation": ["delegate_permission/common.handle_all_urls"],
     "target": {
       "namespace": "android_app",
       "package_name": "com.suncar.suncartrabajador",
       "sha256_cert_fingerprints": [
         "04:9E:C7:2F:93:A4:8E:C5:AF:05:71:B7:AC:66:96:BC:83:D8:7B:18:C9:9E:5A:77:FC:15:31:EE:05:96:80:AD",
         "TU:SHA256:DE:PRODUCCION:AQUI"
       ]
     }
   }
   ```

## 🧪 Testing

1. **Prueba con ADB primero**:
   ```bash
   adb shell am start -a android.intent.action.VIEW -d "https://api.suncarsrl.com/app/crear/averia/12345"
   ```

2. **Prueba desde WhatsApp**:
   - Envíate el enlace: `https://api.suncarsrl.com/app/crear/averia/12345`
   - Debe aparecer como enlace azul clickeable
   - Al tocarlo debe abrir tu app directamente

## 📝 Contenido del archivo assetlinks.json

El archivo `assetlinks.json` que debes servir está en la raíz del proyecto Android:
```
SuncarTrabajador/assetlinks.json
```

Copia ese archivo exacto a tu backend FastAPI.
