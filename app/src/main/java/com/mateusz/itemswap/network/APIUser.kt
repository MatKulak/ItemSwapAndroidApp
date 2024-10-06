package com.mateusz.itemswap.network

import com.mateusz.itemswap.data.RegisterRequest
import com.mateusz.itemswap.data.SimpleValidationRequest
import com.mateusz.itemswap.data.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface APIUser {

    @POST("/api/users/validate")
    fun validate(@Body validationRequest: SimpleValidationRequest): Call<Boolean>

    @POST("/api/users/register")
    fun register(@Body registerRequest: RegisterRequest): Call<User>
}