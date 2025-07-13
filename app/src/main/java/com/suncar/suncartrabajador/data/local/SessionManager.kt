package com.suncar.suncartrabajador.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

data class SessionData(
    val ci: String,
    val password: String
)

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "suncar_session",
        Context.MODE_PRIVATE
    )
    private val gson = Gson()
    private val sessionKey = "session_data"

    /**
     * Guarda las credenciales de sesión en el almacenamiento local
     */
    fun saveSession(ci: String, password: String) {
        val sessionData = SessionData(ci, password)
        val json = gson.toJson(sessionData)
        sharedPreferences.edit { putString(sessionKey, json) }
    }

    /**
     * Obtiene las credenciales de sesión almacenadas
     */
    fun getSession(): SessionData? {
        val json = sharedPreferences.getString(sessionKey, null)
        return if (json != null) {
            try {
                gson.fromJson(json, SessionData::class.java)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    /**
     * Verifica si hay una sesión almacenada
     */
    fun hasSession(): Boolean {
        return getSession() != null
    }

    /**
     * Elimina la sesión almacenada (logout)
     */
    fun clearSession() {
        sharedPreferences.edit().remove(sessionKey).apply()
    }
} 