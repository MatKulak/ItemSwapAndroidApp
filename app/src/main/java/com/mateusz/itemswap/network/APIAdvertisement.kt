package com.mateusz.itemswap.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface APIAdvertisement {

    @Multipart
    @POST("/api/advertisements/add")
    fun uploadFiles(
        @Part files: List<MultipartBody.Part>,
        @Part("data") requestBody: RequestBody
    ): Call<ResponseBody>

    @GET("/api/advertisements")
    fun getAllAdvertisements(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @QueryMap filters: Map<String, String>
    ): Call<ResponseBody>
}