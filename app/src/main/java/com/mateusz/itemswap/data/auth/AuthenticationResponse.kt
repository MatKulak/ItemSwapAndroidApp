package com.mateusz.itemswap.data.auth

import com.mateusz.itemswap.data.user.UserResponse

data class AuthenticationResponse(
    val token: String,
    val userResponse: UserResponse
)
