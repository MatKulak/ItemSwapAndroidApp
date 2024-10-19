package com.mateusz.itemswap.data.advertisement

data class DetailedAdvertisementWithFilesResponse(
    val detailedAdvertisement: DetailedAdvertisementResponse,
    val files: List<String>
)
