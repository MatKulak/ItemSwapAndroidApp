package com.mateusz.itemswap.data.user

import java.util.UUID

data class User (
    val id: UUID,
    val name: String,
    val surname: String,
    val username: String,
    val email: String,
    val phoneNumber: String,
)

