package com.mateusz.itemswap.data.advertisement

import com.mateusz.itemswap.data.localization.LocalizationResponse
import com.mateusz.itemswap.data.category.MainCategoryResponse
import com.mateusz.itemswap.data.user.UserResponse
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
