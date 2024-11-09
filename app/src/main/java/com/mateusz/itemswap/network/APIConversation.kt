package com.mateusz.itemswap.network

import com.mateusz.itemswap.data.conversation.ConversationResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.UUID

interface APIConversation {

    @GET("/api/conversations/{id}")
    fun getConversationByAdvertisementId(
        @Path("id") id: UUID
    ): Call<ConversationResponse>
}