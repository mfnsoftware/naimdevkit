package com.naimyag.androiddevkit.api

import android.content.Context
import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.naimyag.androiddevkit.utils.ILog
import com.naimyag.androiddevkit.utils.IToast
import com.naimyag.androiddevkit.utils.ToastType
import org.json.JSONArray
import java.io.File

@Keep
object ApiCall : ILog, IToast {

    fun postParamRespJson(
        uri: String,
        paramsMap: HashMap<String, Any>,
        responseListener: (Result<JsonObject>?) -> Unit
    ) {
        RequestMgr.postParamRespJson(uri, paramsMap, responseListener)
    }

    fun postParamRespJArray(
        uri: String,
        paramsMap: HashMap<String, Any>,
        responseListener: (Result<JsonArray>?) -> Unit
    ) {
        RequestMgr.postParamRespJArray(uri, paramsMap, responseListener)
    }

    fun getJson(
        uri: String,
        paramsMap: HashMap<String, Any>? = null,
        responseListener: (Result<JsonObject>?) -> Unit
    ) {
        RequestMgr.getJson(uri, paramsMap, responseListener)
    }

    fun getJArray(
        uri: String,
        paramsMap: HashMap<String, Any>? = null,
        responseListener: (Result<JsonArray>?) -> Unit
    ) {
        RequestMgr.getJArray(uri, paramsMap, responseListener)
    }

    fun postParamRespString(
        uri: String,
        paramsMap: HashMap<String, Any>? = null,
        responseListener: (Result<String>?) -> Unit
    ) {
        RequestMgr.postParamRespString(uri, paramsMap, responseListener)
    }

    fun postJsonRespJson(
        uri: String,
        paramsJson: JsonObject,
        responseListener: (Result<JsonObject>?) -> Unit
    ) {
        RequestMgr.postJsonRespJson(uri, paramsJson, responseListener)
    }

    fun postJsonRespString(
        uri: String,
        paramsJson: JsonObject,
        responseListener: (Result<String>?) -> Unit
    ) {
        RequestMgr.postJsonRespString(uri, paramsJson, responseListener)
    }

    fun postJsonRespBool(
        uri: String,
        paramsJson: JsonObject,
        responseListener: (Result<Boolean>?) -> Unit
    ) {
        RequestMgr.postJsonRespBool(uri, paramsJson, responseListener)
    }

    fun uploadFile(
        uri: String,
        file: File,
        fileName: String? = null,
        uploadCallbacks: UploadCallbacks,
        responseListener: (status: Boolean, result: String) -> Unit
    ) {
        RequestMgr.uploadFile(uri, file, fileName, uploadCallbacks, responseListener)
    }

    fun downloadFile(
        uri: String,
        downloadCallbacks: DownloadCallbacks
    ) {
        RequestMgr.downloadFile(uri, downloadCallbacks)
    }

    fun parseResponse(
        json: JsonElement?,
        typeOfC: Class<*>,
        key: String? = null,
        context: Context? = null,
        errMsg: String? = null
    ): Any? {

        when {
            json?.isJsonObject == true -> {
                (json as JsonObject).let { jObj ->
                    key?.let { mKey ->
                        when {
                            typeOfC.isArray -> {
                                val jArray = try {
                                    jObj.getAsJsonArray(mKey)
                                } catch (e: Exception) {
                                    printEx("parseResponse() ex: ", e)
                                    //showToast(errMsg, ToastType.warning, context = context)
                                    null
                                }
                                if (jArray.toString() == "[]") {
                                    return -1
                                }

                                return try {
                                    Gson().fromJson(jArray.toString(), typeOfC)
                                } catch (e: Exception) {
                                    printEx("getObjectList() ex1:", e)
                                    null
                                }
                            }
                            else -> {
                                val mJObj = try {
                                    jObj.getAsJsonObject(mKey)
                                } catch (e: Exception) {
                                    printEx("parseResponse() ex2: ", e)
                                    //showToast(errMsg, ToastType.warning, context = context)
                                    null
                                }

                                return try {
                                    Gson().fromJson(mJObj.toString(), typeOfC)
                                } catch (e: Exception) {
                                    printEx("getObjectList() ex3:", e)
                                    null
                                }
                            }
                        }

                    } ?: kotlin.run {
                        return try {
                            Gson().fromJson(jObj.toString(), typeOfC)
                        } catch (e: Exception) {
                            printEx("getObjectList() ex4:", e)
                            null
                        }
                    }
                }
            }
            json?.isJsonArray == true -> {
                (json as JsonArray).let { jArr ->
                    if (jArr.toString() == "[]") {
                        return -1
                    }

                    return try {
                        Gson().fromJson(jArr.toString(), typeOfC)
                    } catch (e: Exception) {
                        printEx("getObjectList() ex5:", e)
                        null
                    }
                }
            }
            else -> {
                return null
            }
        }
    }

    override fun getmSimpleName(): String? {
        return javaClass.simpleName
    }

    override fun getmContext(): Context? {
        return null
    }
}