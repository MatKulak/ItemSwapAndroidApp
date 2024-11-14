package com.mateusz.itemswap.data.auth

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val username: String,
    val phoneNumber: String,
    val email: String,
    val password: String,
)
