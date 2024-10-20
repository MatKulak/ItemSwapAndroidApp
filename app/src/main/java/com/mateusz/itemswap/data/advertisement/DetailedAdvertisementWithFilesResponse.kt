package com.mateusz.itemswap.data.advertisement

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class DetailedAdvertisementWithFilesResponse(
    val detailedAdvertisement: DetailedAdvertisementResponse,
    val fileIds: List<UUID>
) : Parcelable
