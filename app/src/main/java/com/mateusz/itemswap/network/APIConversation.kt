package com.mateusz.itemswap.network

import com.mateusz.itemswap.data.conversation.ConversationResponse
import com.mateusz.itemswap.data.conversation.SimpleConversationResponse
import com.mateusz.itemswap.data.others.Page
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import java.util.UUID

interface APIConversation {

    @GET("/api/conversation")
    fun getConversationByAdvertisementId(
        @Query("advertisementId") advertisementId: UUID,
    ): Call<ConversationResponse>

    @GET("/api/conversations/{id}")
    fun getConversationByConversationId(
        @Path("id") id: UUID,
    ): Call<ConversationResponse>

    @GET("/api/conversations/page")
    fun getAllConversations(
        @QueryMap params: Map<String, String>
    ): Call<Page<SimpleConversationResponse>>
}