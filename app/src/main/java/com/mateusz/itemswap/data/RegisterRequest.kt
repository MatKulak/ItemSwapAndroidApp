package com.mateusz.itemswap.data

data class RegisterRequest(
    val name: String,
    val surname: String,
    val username: String,
    val phoneNumber: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)
