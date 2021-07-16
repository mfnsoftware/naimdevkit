package com.naimyag.androiddevkit.prefs

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.SharedPreferences
import androidx.annotation.Keep
import com.google.gson.Gson
import com.naimyag.androiddevkit.utils.ILog
import java.lang.NullPointerException
import java.util.*

class UpCatcher constructor(context: Context? = null, prefName: String? = null) : ILog {

    @Keep
    val gson = Gson()
    private val map = HashMap<String, Any>()
    private val intmap = HashMap<String, Int>()
    private val boolmap = HashMap<String, Boolean>()
    private val stringmap = HashMap<String, String>()
    private val floatmap = HashMap<String, Float>()

    private lateinit var prefs: SharedPreferences
    private var activityManager: ActivityManager? = null

    companion object {

        @JvmStatic
        private var instance: UpCatcher? = null

        @JvmStatic
        internal fun getInstance(): UpCatcher {
            if (instance == null)
                throw NullPointerException("UpCatcher Not yet initialized!")

            return instance!!
        }
    }


    init {
        if (instance == null) {
            printLog("->init()")
            if (context == null || prefName == null)
                throw NullPointerException("UpCatcher Not yet initialized!")

            prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            instance = this
        } else {
            printLog("UpCatcher ->init() instance already not null")
        }
    }

    @Keep
    fun putObject(key: String, value: Any) {
        map[key] = value
        prefs.edit().putString(key, gson.toJson(value)).apply()
        checkMemory()
    }

    @Keep
    fun putString(key: String, value: String) {
        stringmap[key] = value
        prefs.edit().putString(key, value).apply()
        checkMemory()
    }

    @Keep
    fun putInt(key: String, value: Int) {
        intmap[key] = value
        prefs.edit().putInt(key, value).apply()
        checkMemory()
    }

    @Keep
    fun putFloat(key: String, value: Float) {
        floatmap[key] = value
        prefs.edit().putFloat(key, value).apply()
        checkMemory()
    }

    @Keep
    fun putBool(key: String, value: Boolean) {
        boolmap[key] = value
        prefs.edit().putBoolean(key, value).apply()
        checkMemory()
    }

    @Keep
    fun remove(key: String) {
        dirty(key)
        prefs.edit().remove(key).apply()
    }

    @Keep
    fun getObject(key: String, c: Class<*>?): Any? {
        var s = map[key]
        if (s == null) {
            val json = prefs.getString(key, null)
            if (json != null) {
                try {
                    s = gson.fromJson(json, c)
                } catch (e: Exception) {
                    printEx("getObject gson.fromJson ex key: $key", e)
                }
            }
        }
        return s
    }

    @Keep
    fun getInt(key: String, def: Int): Int {
        var i = intmap[key]
        if (i == null) {
            i = prefs.getInt(key, def)
        }
        return i
    }

    @Keep
    fun getFloat(key: String, def: Float): Float {
        var f = floatmap[key]
        if (f == null) {
            f = prefs.getFloat(key, def)
        }
        return f
    }

    @Keep
    fun getString(key: String, def: String?): String? {
        var s = stringmap[key]
        if (s == null || s.isEmpty()) {
            s = prefs.getString(key, def)
            if (s == null || s.isEmpty()) s = def
        }
        return s
    }

    @Keep
    fun getBool(key: String, def: Boolean): Boolean {
        var b = boolmap[key]
        if (b == null) {
            b = prefs.getBoolean(key, def)
        }
        return b
    }

    private fun dirty(key: String) {
        printLog("->dirty(key $key)")
        map.remove(key)
        stringmap.remove(key)
        intmap.remove(key)
        boolmap.remove(key)
        floatmap.remove(key)
    }

    private fun cleanMaps() {
        printLog("->cleanMaps()")
        map.clear()
        stringmap.clear()
        intmap.clear()
        boolmap.clear()
        floatmap.clear()
    }

    @Keep
    fun getPref(): SharedPreferences {
        return prefs
    }

    private fun checkMemory() {
        if (getAvailableMemory().lowMemory) {
            cleanMaps()
            System.gc()
        }
    }

    private fun getAvailableMemory(): ActivityManager.MemoryInfo {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memoryInfo)
        return memoryInfo
    }

    override fun getmSimpleName(): String {
        return javaClass.simpleName
    }
}