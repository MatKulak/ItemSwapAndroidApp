package com.mateusz.itemswap.data.conversation

import android.os.Parcelable
import com.mateusz.itemswap.data.message.MessageResponse
import com.mateusz.itemswap.data.user.UserResponse
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ConversationResponse(
    var id: UUID?,
    val seller: UserResponse?,
    val buyer: UserResponse?,
    val messages: List<MessageResponse>?
): Parcelable