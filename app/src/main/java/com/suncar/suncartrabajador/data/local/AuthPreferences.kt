package com.suncar.suncartrabajador.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * Maneja el almacenamiento del token JWT y datos básicos del usuario autenticado.
 *
 * Se inicializa una vez con el applicationContext y luego se puede usar
 * desde cualquier parte de la app (por ejemplo, en el interceptor de Retrofit).
 */
object AuthPreferences {
    private const val PREF_NAME = "auth"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_USER_CI = "user_ci"
    private const val KEY_USER_NOMBRE = "user_nombre"
    private const val KEY_USER_ROL = "user_rol"

    @Volatile
    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) {
            synchronized(this) {
                if (prefs == null) {
                    prefs =
                        context.applicationContext.getSharedPreferences(
                            PREF_NAME,
                            Context.MODE_PRIVATE
                        )
                }
            }
        }
    }

    private fun requirePrefs(): SharedPreferences {
        return prefs
            ?: throw IllegalStateException("AuthPreferences no ha sido inicializado. Llama a AuthPreferences.init(context) en el arranque de la app.")
    }

    fun saveAuth(
        token: String,
        ci: String,
        nombre: String,
        rol: String
    ) {
        val p = requirePrefs()
        p.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER_CI, ci)
            .putString(KEY_USER_NOMBRE, nombre)
            .putString(KEY_USER_ROL, rol)
            .apply()
    }

    fun getToken(): String? = prefs?.getString(KEY_TOKEN, null)

    fun clear() {
        prefs?.edit()?.clear()?.apply()
    }

    fun isInitialized(): Boolean = prefs != null
}


