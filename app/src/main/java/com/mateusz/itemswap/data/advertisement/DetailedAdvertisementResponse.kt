package com.mateusz.itemswap.data.advertisement

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mateusz.itemswap.data.localization.LocalizationResponse
import com.mateusz.itemswap.data.user.UserResponse
import kotlinx.parcelize.Parcelize
import java.util.UUID
@Parcelize
data class DetailedAdvertisementResponse(
    val id: UUID,
    val title: String?,
    val condition: String?,
    val city: String?,
    val addDate: String?,
    val description: String?,
    val localizationResponse: LocalizationResponse,
    val userResponse: UserResponse,
    val category: String,
    val phoneNumber: String,
    @SerializedName("isFollowed") val isFollowed: Boolean
) : Parcelable