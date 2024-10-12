package com.mateusz.itemswap.data

import com.mateusz.itemswap.enums.ProductCategories

data class AddAdvertisementRequest(
    val title: String,
    val mainCategory: String,
    val description: String,
    val localization: String,
    val phoneNumber: String,
)
