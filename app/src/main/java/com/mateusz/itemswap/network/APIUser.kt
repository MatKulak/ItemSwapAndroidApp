package com.mateusz.itemswap.network

import com.mateusz.itemswap.data.others.SimpleValidationRequest
import com.mateusz.itemswap.data.user.UpdateUserRequest
import com.mateusz.itemswap.data.user.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface APIUser {

    @POST("/api/users/validate")
    fun validate(@Body validationRequest: SimpleValidationRequest): Call<Boolean>
}