# Uso de la Funcionalidad de Brigada

## Descripción

La aplicación ahora maneja la información de brigadas que incluye un líder y sus integrantes. Esta funcionalidad se activa cuando un usuario inicia sesión exitosamente y se almacena de manera global usando el patrón Singleton.

## Estructura de Respuesta del Login

La respuesta del servidor ahora incluye información de brigada:

```json
{
  "success": true,
  "message": "Autenticación exitosa",
  "brigada": {
    "lider_ci": "12345678",
    "lider": {
      "CI": "12345678",
      "nombre": "Juan Pérez",
      "contraseña": "12345678*"
    },
    "integrantes": [
      {
        "CI": "56789012",
        "nombre": "Luis Fernández",
        "contraseña": null
      },
      {
        "CI": "67890123",
        "nombre": "Laura López",
        "contraseña": null
      },
      {
        "CI": "78901234",
        "nombre": "Pedro Sánchez",
        "contraseña": null
      }
    ]
  }
}
```

## Modelos de Datos

### LoginResponse

```kotlin
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val brigada: BrigadaData? = null
)
```

### BrigadaData

```kotlin
data class BrigadaData(
    val liderCi: String,
    val lider: IntegranteData,
    val integrantes: List<IntegranteData>
)
```

### IntegranteData

```kotlin
data class IntegranteData(
    val ci: String,
    val nombre: String,
    val contraseña: String? = null
)
```

## BrigadaManager Singleton

El `BrigadaManager` es un Singleton que almacena la información de la brigada de manera global en la aplicación.

### Características Principales

- **Almacenamiento Global**: La información de brigada está disponible en toda la aplicación
- **StateFlow**: Usa StateFlow para observar cambios reactivamente
- **Thread-Safe**: Implementación thread-safe para acceso concurrente
- **Métodos de Utilidad**: Proporciona métodos útiles para verificar roles y obtener información

### Uso del BrigadaManager

#### 1. Almacenar Información de Brigada (Login Exitoso)

```kotlin
// En LoginViewModel
when (result) {
    is LoginResult.Success -> {
        val user = result.user

        // Almacenar la información en el BrigadaManager
        if (user.brigada != null) {
            BrigadaManager.setBrigada(user, user.brigada)
        } else {
            BrigadaManager.setUser(user)
        }
    }
}
```

#### 2. Observar Cambios en la Brigada

```kotlin
// En cualquier ViewModel
viewModelScope.launch {
    BrigadaManager.currentBrigada.collect { brigada ->
        // Reaccionar a cambios en la brigada
        updateUI(brigada)
    }
}

viewModelScope.launch {
    BrigadaManager.currentUser.collect { user ->
        // Reaccionar a cambios en el usuario
        updateUserInfo(user)
    }
}
```

#### 3. Verificar Roles y Estado

```kotlin
// Verificar si el usuario actual es líder
val isLeader = BrigadaManager.isCurrentUserLeader()

// Verificar si hay una brigada activa
val hasBrigada = BrigadaManager.hasActiveBrigada()

// Verificar si hay un usuario autenticado
val isAuthenticated = BrigadaManager.isUserAuthenticated()
```

#### 4. Obtener Información de la Brigada

```kotlin
// Obtener la brigada actual
val brigada = BrigadaManager.getCurrentBrigada()

// Obtener el usuario actual
val user = BrigadaManager.getCurrentUser()

// Obtener lista de integrantes
val integrantes = BrigadaManager.getIntegrantes()

// Obtener información del líder
val lider = BrigadaManager.getLider()
```

#### 5. Limpiar Información (Logout)

```kotlin
// Limpiar toda la información de brigada y usuario
BrigadaManager.clear()
```

### Métodos Disponibles

| Método                      | Descripción                          |
| --------------------------- | ------------------------------------ |
| `setBrigada(user, brigada)` | Establece usuario y brigada          |
| `setUser(user)`             | Establece solo usuario (sin brigada) |
| `clear()`                   | Limpia toda la información (logout)  |
| `getCurrentBrigada()`       | Obtiene brigada actual               |
| `getCurrentUser()`          | Obtiene usuario actual               |
| `isCurrentUserLeader()`     | Verifica si usuario es líder         |
| `getIntegrantes()`          | Obtiene lista de integrantes         |
| `getLider()`                | Obtiene información del líder        |
| `hasActiveBrigada()`        | Verifica si hay brigada activa       |
| `isUserAuthenticated()`     | Verifica si hay usuario autenticado  |

## Uso en el Código

### 1. Login y Almacenamiento Automático

```kotlin
// En tu ViewModel o Repository
val result = repository.login(ci, password)

when (result) {
    is LoginResult.Success -> {
        val user = result.user

        // Almacenar automáticamente en BrigadaManager
        if (user.brigada != null) {
            BrigadaManager.setBrigada(user, user.brigada)
        } else {
            BrigadaManager.setUser(user)
        }
    }
    is LoginResult.Error -> {
        // Manejar error
    }
}
```

### 2. Acceso desde Cualquier Parte de la App

```kotlin
// En cualquier ViewModel o Activity
class MyViewModel : ViewModel() {
    init {
        // Observar cambios en la brigada
        viewModelScope.launch {
            BrigadaManager.currentBrigada.collect { brigada ->
                // Actualizar UI cuando cambie la brigada
                updateBrigadaUI(brigada)
            }
        }
    }

    fun showBrigadaInfo() {
        val brigada = BrigadaManager.getCurrentBrigada()
        val user = BrigadaManager.getCurrentUser()

        if (brigada != null && user != null) {
            val role = if (BrigadaManager.isCurrentUserLeader()) "Líder" else "Integrante"
            println("Usuario: ${user.name}, Rol: $role")
            println("Líder: ${brigada.lider.nombre}")
            println("Integrantes: ${brigada.integrantes.size}")
        }
    }
}
```

### 3. Verificar Permisos por Rol

```kotlin
fun canEditBrigada(): Boolean {
    return BrigadaManager.isCurrentUserLeader()
}

fun canViewIntegrantes(): Boolean {
    return BrigadaManager.hasActiveBrigada()
}

fun canAccessAdminFeatures(): Boolean {
    return BrigadaManager.isCurrentUserLeader() && BrigadaManager.isUserAuthenticated()
}
```

## Flujo de Autenticación

1. **Usuario ingresa CI y contraseña**
2. **Se valida el formulario** usando `LoginValidator`
3. **Se hace la petición al servidor** usando `AuthService`
4. **Se procesa la respuesta** y se convierte a modelos internos
5. **Se crea el objeto User** con información de la brigada
6. **Se almacena en BrigadaManager** usando `setBrigada()` o `setUser()`
7. **Se retorna LoginResult.Success** con el usuario y su brigada

## Consideraciones

- La información de la brigada es opcional (`brigada: Brigada? = null`)
- Solo el líder tiene contraseña en la respuesta
- Los integrantes no tienen contraseña (`contraseña: null`)
- La aplicación maneja tanto usuarios con brigada como sin brigada
- El `BrigadaManager` mantiene la información durante toda la sesión
- La información se limpia automáticamente al hacer logout

## Próximos Pasos

- Implementar navegación a pantallas específicas según el rol (líder vs integrante)
- Crear pantallas para gestionar la brigada
- Implementar funcionalidades específicas para líderes e integrantes
- Agregar persistencia local de la información de brigada
- Implementar sincronización en tiempo real de cambios en la brigada
