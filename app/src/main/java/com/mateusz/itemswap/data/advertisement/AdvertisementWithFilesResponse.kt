package com.mateusz.itemswap.data.advertisement

data class AdvertisementWithFilesResponse(
    val advertisementResponse: AdvertisementResponse,
    val files: List<ByteArray>
)