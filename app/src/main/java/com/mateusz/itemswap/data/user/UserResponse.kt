package com.mateusz.itemswap.data.user

import java.util.UUID

data class UserResponse(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val phoneNumber: String
)