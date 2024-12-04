package com.mateusz.itemswap.data.advertisement

data class AddAdvertisementRequest(
    val category: String,
    val condition: String,
    val title: String,
    val description: String,
    val city: String,
    val street: String,
    val postalCode: String,
    val phoneNumber: String,
    val trade: String
)
