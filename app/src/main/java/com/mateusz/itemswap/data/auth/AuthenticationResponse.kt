package com.mateusz.itemswap.data.auth

import com.mateusz.itemswap.data.user.User

data class AuthenticationResponse(
    val token: String,
    val user: User
)
