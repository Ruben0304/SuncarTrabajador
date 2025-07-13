package com.suncar.suncartrabajador.utils

import android.content.Context
import android.content.pm.PackageManager

object AppUtils {

    fun getAppVersion(context: Context): String? {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "1.0.0" // Versión por defecto si no se puede obtener
        }
    }

    fun getAppVersionCode(context: Context): Int {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            1 // Código de versión por defecto
        }
    }
}
