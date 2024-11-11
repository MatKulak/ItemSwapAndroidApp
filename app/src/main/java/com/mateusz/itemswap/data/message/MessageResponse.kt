package com.mateusz.itemswap.data.message

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class MessageResponse(
    val content: String,
    val sendDate: String,
    val senderId: UUID,
    val senderUsername: String
): Parcelable
