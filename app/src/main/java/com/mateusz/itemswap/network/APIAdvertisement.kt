package com.mateusz.itemswap.network

import com.mateusz.itemswap.data.advertisement.AdvertisementWithFileResponse
import com.mateusz.itemswap.data.advertisement.DetailedAdvertisementResponse
import com.mateusz.itemswap.data.others.Page
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.QueryMap
import java.util.UUID

interface APIAdvertisement {

    @Multipart
    @POST("/api/advertisements/add")
    fun addAdvertisement(
        @Part files: List<MultipartBody.Part>,
        @Part("data") requestBody: RequestBody
    ): Call<ResponseBody>

    @Multipart
    @PATCH("/api/advertisements/{id}/update")
    fun updateAdvertisement(
        @Path("id") id: UUID,
        @Part files: List<MultipartBody.Part>,
        @Part("data") requestBody: RequestBody
    ): Call<ResponseBody>

    @GET("/api/advertisements/page")
    fun getAllAdvertisements(
        @QueryMap params: Map<String, String>
    ): Call<Page<AdvertisementWithFileResponse>>

    @GET("/api/advertisements/{id}")
    fun getAdvertisementById(
        @Path("id") id: UUID
    ): Call<DetailedAdvertisementResponse>

    @GET("/api/advertisements/{id}/files")
    fun getAdvertisementFiles(
        @Path("id") id: UUID
    ): Call<List<String>>

    @POST("/api/advertisements/{id}/followers")
    fun updateAdvertisementFollowers(
        @Path("id") id: UUID
    ): Call<Boolean>
}