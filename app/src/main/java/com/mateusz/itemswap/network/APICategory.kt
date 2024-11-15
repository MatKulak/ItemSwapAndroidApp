package com.mateusz.itemswap.network

import retrofit2.Call
import retrofit2.http.GET

interface APICategory {

    @GET("/api/categories")
    fun getAll(): Call<List<String>>
}