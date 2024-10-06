package com.mateusz.itemswap.network

import com.mateusz.itemswap.auth.dto.LoginRequest
import com.mateusz.itemswap.auth.dto.AuthenticationResponse
import com.mateusz.itemswap.data.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface APIAuthenticate {

    @POST("/auth/register")
    fun register(@Body registerRequest: RegisterRequest): Call<AuthenticationResponse>

    @POST("/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<AuthenticationResponse>
}