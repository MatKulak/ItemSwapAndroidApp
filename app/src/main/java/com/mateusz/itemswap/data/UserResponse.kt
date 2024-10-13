package com.mateusz.itemswap.data

import java.util.UUID

data class UserResponse(
    val id: UUID,
    val name: String,
    val surname: String,
    val username: String,
    val email: String,
    val phoneNumber: String
)