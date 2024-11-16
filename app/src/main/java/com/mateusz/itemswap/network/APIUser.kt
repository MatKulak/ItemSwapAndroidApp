package com.mateusz.itemswap.network

import com.mateusz.itemswap.data.others.SimpleValidationRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface APIUser {

    @POST("/api/users/validate")
    fun validate(@Body validationRequest: SimpleValidationRequest): Call<Boolean>
}