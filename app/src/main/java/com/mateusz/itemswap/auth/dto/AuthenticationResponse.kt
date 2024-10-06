package com.mateusz.itemswap.auth.dto

import com.mateusz.itemswap.data.User

data class AuthenticationResponse(
    val token: String,
    val user: User
)
