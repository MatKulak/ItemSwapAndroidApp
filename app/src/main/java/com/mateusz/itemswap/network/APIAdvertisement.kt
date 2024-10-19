package com.mateusz.itemswap.network

import com.mateusz.itemswap.data.advertisement.AdvertisementWithFileResponse
import com.mateusz.itemswap.data.advertisement.DetailedAdvertisementWithFilesResponse
import com.mateusz.itemswap.ztest.Page
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface APIAdvertisement {

    @Multipart
    @POST("/api/advertisements/add")
    fun uploadFiles(
        @Part files: List<MultipartBody.Part>,
        @Part("data") requestBody: RequestBody
    ): Call<ResponseBody>

    @GET("/api/advertisements/page")
    fun getAllAdvertisements(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<Page<AdvertisementWithFileResponse>>

    @GET("/api/advertisements/{id}")
    fun getOneById(
        @Path("id") id: UUID
    ): Call<DetailedAdvertisementWithFilesResponse>
}