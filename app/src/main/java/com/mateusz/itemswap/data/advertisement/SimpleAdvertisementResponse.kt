package com.mateusz.itemswap.data.advertisement

import java.util.UUID

data class SimpleAdvertisementResponse(
    val id: UUID,
    val title: String,
    val condition: String,
    val city: String,
    val street: String,
    val postalCode: String,
    val addDate: String,
    val trade: String
)
