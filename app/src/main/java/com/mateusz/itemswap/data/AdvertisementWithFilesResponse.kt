package com.mateusz.itemswap.data

data class AdvertisementWithFilesResponse(
    val advertisementResponse: AdvertisementResponse,
    val files: List<ByteArray> // Assuming files are returned as byte arrays, adjust if needed
)