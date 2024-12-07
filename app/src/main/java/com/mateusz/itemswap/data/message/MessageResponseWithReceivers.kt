package com.mateusz.itemswap.data.message

import java.util.UUID

data class MessageResponseWithReceivers(
    val messageResponse: MessageResponse,
    val receiverIds: Set<UUID>,
    val conversationId: UUID
)