package com.naimyag.androiddevkit.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import retrofit2.http.*

interface IApis {

    @POST
    fun postString(@Url uri: String): Single<String>

    @GET
    fun getJson(@Url uri: String): Single<JsonObject>

    @GET
    fun getJArray(@Url uri: String): Single<JsonArray>

    @FormUrlEncoded
    @POST
    fun postParamRespJson(
        @Url uri: String,
        @FieldMap bodyParams: HashMap<String, Any>
    ): Single<JsonObject>

    @POST
    fun postJsonRespJson(@Url uri: String, @Body bodyJson: JsonObject): Single<JsonObject>

    @POST
    fun postJsonRespString(@Url uri: String, @Body bodyJson: JsonObject): Single<String>

    @POST
    fun postJsonRespBool(@Url uri: String, @Body bodyJson: JsonObject): Single<Boolean>

    @FormUrlEncoded
    @POST
    fun postParamRespString(
        @Url uri: String,
        @FieldMap bodyParams: HashMap<String, Any>
    ): Single<String>

    @FormUrlEncoded
    @POST
    fun postParamRespJArray(
        @Url uri: String,
        @FieldMap bodyParams: HashMap<String, Any>
    ): Single<JsonArray>


    @Multipart
    @POST
    fun uploadFile(
        @Url uri: String,
        @Part file: MultipartBody.Part
    ): Single<String>

    @Streaming
    @GET
    fun downloadFile(
        @Url uri: String
    ): Flowable<String>
}