package com.mateusz.itemswap.data.advertisement

data class AdvertisementWithFileResponse(
    val simpleAdvertisementResponse: SimpleAdvertisementResponse,
    val file: String?
)