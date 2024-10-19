package com.mateusz.itemswap.ztest

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/strings")
    fun getStrings(@Query("page") page: Int, @Query("size") size: Int): Call<Page<String>>

//    @GET("/api/advertisements/page")
//    fun getAllAdvertisements(
//        @Query("page") page: Int,
//        @Query("size") size: Int
//    ): Call<Page<AdvertisementWithFileResponse>>
}
