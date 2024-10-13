package com.mateusz.itemswap.data

import java.time.LocalDateTime
import java.util.UUID

data class AdvertisementResponse(
    val id: UUID,
    val title: String,
    val description: String,
    val phoneNumber: String,
    val addDate: LocalDateTime,
    val localizationResponse: LocalizationResponse,
    val mainCategoryResponse: MainCategoryResponse,
    val userResponse: UserResponse
)
