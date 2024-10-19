package com.mateusz.itemswap.data.advertisement

data class AddAdvertisementRequest(
    val title: String,
    val mainCategory: String,
    val description: String,
    val localization: String,
    val phoneNumber: String,
)
