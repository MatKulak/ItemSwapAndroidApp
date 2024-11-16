package com.mateusz.itemswap.network

import com.mateusz.itemswap.data.auth.LoginRequest
import com.mateusz.itemswap.data.auth.AuthenticationResponse
import com.mateusz.itemswap.data.auth.RegisterRequest
import com.mateusz.itemswap.data.user.UpdateUserRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface APIAuthenticate {

    @POST("/auth/register")
    fun register(@Body registerRequest: RegisterRequest): Call<AuthenticationResponse>

    @POST("/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<AuthenticationResponse>

    @POST("/logout")
    fun logout(): Call<Void>

    @GET("/auth/logged-in")
    fun isLoggedIn(): Call<Boolean>

    @PATCH("/auth/{id}/update")
    fun updateUser(@Path("id") id: UUID, @Body updateUserRequest: UpdateUserRequest): Call<AuthenticationResponse>
}