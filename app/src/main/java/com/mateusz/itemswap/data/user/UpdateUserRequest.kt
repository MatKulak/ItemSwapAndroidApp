package com.mateusz.itemswap.data.user

data class UpdateUserRequest(
    val firstName: String,
    val lastName: String,
    val username: String,
    val phoneNumber: String,
    val email: String,
    val updatePassword: Boolean,
    val password: String?,
)
