package com.mateusz.itemswap.data.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class UserResponse(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val phoneNumber: String
) : Parcelable