package com.mateusz.itemswap.data.conversation

import com.mateusz.itemswap.data.message.MessageResponse
import com.mateusz.itemswap.data.user.UserResponse
import java.util.UUID

data class ConversationResponse(
    val id: UUID,
    val seller: UserResponse,
    val buyer: UserResponse,
    val messages: List<MessageResponse>
)