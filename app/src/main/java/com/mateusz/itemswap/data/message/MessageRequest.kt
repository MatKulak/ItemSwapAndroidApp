package com.mateusz.itemswap.data.message

import java.util.UUID

data class MessageRequest(
    val conversationId: UUID?,
    val advertisementId: UUID,
    val senderId: UUID,
    val content: String
)
