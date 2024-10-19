package com.mateusz.itemswap.data.advertisement

import java.util.UUID

data class SimpleAdvertisementResponse(
    val id: UUID,
    val title: String,
    val condition: String,
    val city: String,
    val addDate: String,
)
