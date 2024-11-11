package com.mateusz.itemswap.data.conversation

import java.util.UUID

data class SimpleConversationResponse(
    val advertisementTitle: String,
    val username: String,
    val advertisementId: UUID,
    val participantId: UUID
)