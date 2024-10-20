package com.mateusz.itemswap.data.localization

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocalizationResponse(
    val city: String?,
    val postalCode: String?,
    val street: String?
) : Parcelable