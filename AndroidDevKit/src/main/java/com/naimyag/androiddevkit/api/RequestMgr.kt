package com.naimyag.androiddevkit.api

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.naimyag.androiddevkit.utils.ILog
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subscribers.DisposableSubscriber
import okhttp3.*
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RequestMgr : ILog, IHeader {

    private var headersMap: HashMap<String, String>? = null

    private val disposable = CompositeDisposable()

    private var gson = GsonBuilder()
        .setLenient()
        .create()

    private var api = Retrofit.Builder()
        .baseUrl("https://google.com")
        .client(getUnsafeOkHttpClient())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(IApis::class.java)

    private fun reInitApi() {

        api = Retrofit.Builder()
            .baseUrl("https://google.com")
            .client(getUnsafeOkHttpClient())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(IApis::class.java)
    }

    internal fun postParamRespJson(
        uri: String,
        paramsMap: HashMap<String, Any>,
        responseListener: (Result<JsonObject>?) -> Unit
    ) {
        disposable.add(
            api.postParamRespJson(uri, paramsMap).subscribeOn(
                Schedulers.newThread()
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<JsonObject>() {
                    override fun onSuccess(t: JsonObject) {
                        responseListener(Result.Success(t))
                    }

                    override fun onError(e: Throwable) {
                        printEx("postParamRespJson() ex:", Exception(e))
                        responseListener(Result.Error(e as HttpException))
                    }
                })
        )
    }

    internal fun postParamRespJArray(
        uri: String,
        paramsMap: HashMap<String, Any>,
        responseListener: (Result<JsonArray>?) -> Unit
    ) {

        disposable.add(
            api.postParamRespJArray(uri, paramsMap).subscribeOn(
                Schedulers.newThread()
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<JsonArray>() {
                    override fun onSuccess(t: JsonArray) {
                        responseListener(Result.Success(t))
                    }

                    override fun onError(e: Throwable) {
                        printEx("postParamRespJArray() Ex:", Exception(e))
                        responseListener(Result.Error(e as HttpException))
                    }
                })
        )
    }

    internal fun getJson(
        uri: String,
        paramsMap: HashMap<String, Any>?,
        responseListener: (Result<JsonObject>?) -> Unit
    ) {
        var mUri = uri
        var indx = 0
        paramsMap?.forEach {
            if (indx == 0) {
                mUri += "?"
            }
            indx++
            mUri += "${it.key}=${it.value}"
            if (paramsMap.size != indx) {
                mUri += "&"
            }
        }

        disposable.add(
            api.getJson(mUri).subscribeOn(
                Schedulers.newThread()
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<JsonObject>() {
                    override fun onSuccess(t: JsonObject) {
                        responseListener(Result.Success(t))
                    }

                    override fun onError(e: Throwable) {
                        printEx("getJson() Ex:", Exception(e))
                        responseListener(Result.Error(e as HttpException))
                    }
                })
        )
    }

    internal fun getJArray(
        uri: String,
        paramsMap: HashMap<String, Any>?,
        responseListener: (Result<JsonArray>?) -> Unit
    ) {
        var mUri = uri
        var indx = 0
        paramsMap?.forEach {
            if (indx == 0) {
                mUri += "?"
            }
            indx++
            mUri += "${it.key}=${it.value}"
            if (paramsMap.size != indx) {
                mUri += "&"
            }
        }

        disposable.add(
            api.getJArray(mUri).subscribeOn(
                Schedulers.newThread()
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<JsonArray>() {
                    override fun onSuccess(t: JsonArray) {
                        responseListener(Result.Success(t))
                    }

                    override fun onError(e: Throwable) {
                        printEx("getJson() Ex:", Exception(e))
                        responseListener(Result.Error(e as HttpException))
                    }
                })
        )
    }

    internal fun postParamRespString(
        uri: String,
        paramsMap: HashMap<String, Any>?,
        responseListener: (Result<String>?) -> Unit
    ) {

        paramsMap?.let { prms ->
            disposable.add(
                api.postParamRespString(uri, prms).subscribeOn(
                    Schedulers.newThread()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<String>() {
                        override fun onSuccess(t: String) {
                            responseListener(Result.Success(t))
                        }

                        override fun onError(e: Throwable) {
                            printEx("postParamRespString() Ex:", Exception(e))
                            responseListener(Result.Error(e as HttpException))
                        }
                    })
            )
        } ?: kotlin.run {
            disposable.add(
                api.postString(uri).subscribeOn(
                    Schedulers.newThread()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<String>() {
                        override fun onSuccess(t: String) {
                            responseListener(Result.Success(t))
                        }

                        override fun onError(e: Throwable) {
                            printEx("postString() Ex:", Exception(e))
                            responseListener(Result.Error(e as HttpException))
                        }
                    })
            )
        }
    }

    internal fun postJsonRespJson(
        uri: String,
        paramsJson: JsonObject,
        responseListener: (Result<JsonObject>?) -> Unit
    ) {

        disposable.add(
            api.postJsonRespJson(uri, paramsJson).subscribeOn(
                Schedulers.newThread()
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<JsonObject>() {
                    override fun onSuccess(t: JsonObject) {
                        responseListener(Result.Success(t))
                    }

                    override fun onError(e: Throwable) {
                        printEx("postJsonRespJson() Ex:", Exception(e))
                        responseListener(Result.Error(e as HttpException))
                    }
                })
        )
    }

    internal fun postJsonRespString(
        uri: String,
        paramsJson: JsonObject,
        responseListener: (Result<String>?) -> Unit
    ) {

        disposable.add(
            api.postJsonRespString(uri, paramsJson).subscribeOn(
                Schedulers.newThread()
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<String>() {
                    override fun onSuccess(t: String) {
                        responseListener(Result.Success(t))
                    }

                    override fun onError(e: Throwable) {
                        printEx("postJsonRespString() Ex:", Exception(e))
                        responseListener(Result.Error(e as HttpException))
                    }
                })
        )
    }

    internal fun postJsonRespBool(
        uri: String,
        paramsJson: JsonObject,
        responseListener: (Result<Boolean>?) -> Unit
    ) {

        disposable.add(
            api.postJsonRespBool(uri, paramsJson).subscribeOn(
                Schedulers.newThread()
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Boolean>() {
                    override fun onSuccess(t: Boolean) {
                        responseListener(Result.Success(t))
                    }

                    override fun onError(e: Throwable) {
                        printEx("postJsonRespBool() Ex:", Exception(e))
                        responseListener(Result.Error(e as HttpException))
                    }
                })
        )
    }

    internal fun uploadFile(
        uri: String,
        file: File,
        fileName: String?,
        uploadCallbacks: UploadCallbacks,
        responseListener: (status: Boolean, result: String) -> Unit
    ) {

        val fileBody = ProgressRequestBody(file, uploadCallbacks)
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData(
                ProgressRequestBody.getMimeType(file.path)!!,
                fileName ?: file.name,
                fileBody
            )

        disposable.add(
            api.uploadFile(uri, body).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<String>() {
                    override fun onSuccess(t: String) {
                        responseListener(true, t)
                    }

                    override fun onError(e: Throwable) {
                        var err = ""
                        e.message?.let {
                            err = it
                        } ?: e.localizedMessage?.let {
                            err = it
                        }

                        printEx("uploadFile() ex: ", Exception(e))
                        responseListener(false, err)
                    }
                })
        )
    }

    internal fun downloadFile(
        uri: String,
        downloadCallbacks: DownloadCallbacks
    ) {
        disposable.add(
            api.downloadFile(uri)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSubscriber<String>() {
                    override fun onNext(t: String?) {
                        downloadCallbacks.onNext(t)
                    }

                    override fun onError(t: Throwable?) {
                        downloadCallbacks.onError(t)
                    }

                    override fun onComplete() {
                        downloadCallbacks.onComplete()
                    }
                })
        )
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers() = arrayOf<X509Certificate>()
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        val sslSocketFactory = sslContext.socketFactory

        return OkHttpClient.Builder()
            .apply {
                getHeaderInterceptor()?.let {
                    addInterceptor(it)
                }
            }
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }.build()
    }

    private fun getHeaderInterceptor(): Interceptor? {
        return if (getHeaders() != null)
            Interceptor { chain ->
                val request =
                    chain.request().newBuilder()
                        .apply {
                            getHeaders()?.forEach { mHeader ->
                                header(mHeader.key, mHeader.value)
                            }
                        }
                        .build()
                chain.proceed(request)
            }
        else {
            null
        }
    }

    override fun getmSimpleName(): String {
        return javaClass.simpleName
    }

    override fun getHeaders(): HashMap<String, String>? {
        return headersMap
    }

    override fun setHeaders(headersMap: HashMap<String, String>?) {
        this.headersMap = headersMap
        reInitApi()
    }


}