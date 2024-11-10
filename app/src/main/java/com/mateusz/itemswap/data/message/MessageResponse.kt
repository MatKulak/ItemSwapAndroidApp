package com.mateusz.itemswap.data.message

import java.util.UUID

data class MessageResponse(
    val content: String,
    val sendDate: String,
    val senderId: UUID,
    val senderUsername: String
)
