package com.mateusz.itemswap.data.advertisement

import com.mateusz.itemswap.data.localization.LocalizationResponse
import com.mateusz.itemswap.data.user.UserResponse
import java.util.UUID

data class DetailedAdvertisementResponse(
    val id: UUID,
    val title: String,
    val condition: String,
    val city: String,
    val addDate: String,
    val description: String,
    val localizationResponse: LocalizationResponse,
    val userResponse: UserResponse
)