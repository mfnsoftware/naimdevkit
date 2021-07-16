package com.naimyag.androiddevkit.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.annotation.Keep

@Keep
object Utils : ILog {

    fun isPackageInstalled(packagename: String, context: Context): Boolean {
        return try {
            val pm = context.packageManager
            pm.getPackageInfo(packagename, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun getAppVersion(context: Context): String {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val version = pInfo.versionName
        // val deviceModel = "${DeviceName.getDeviceInfo(applicationContext).marketName} ${DeviceName.getDeviceInfo(applicationContext).model}"
        return version
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(appContext: Context): String? {
        try {
            Settings.Secure.getString(
                appContext.contentResolver,
                Settings.Secure.ANDROID_ID
            )?.let { deviceId ->
                printLog("->getDeviceId() return: $deviceId")
                return deviceId
            } ?: kotlin.run {
                printLog("->getDeviceId() return: null")
                return null
            }
        } catch (e: Exception) {
            printEx("getDeviceId() deviceId ex:", e)
            printLog("->getDeviceId() return: null")
            return null
        }
    }

    override fun getmSimpleName(): String {
        return "NDK_${javaClass.simpleName}"
    }
}