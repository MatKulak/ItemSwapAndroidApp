package com.mateusz.itemswap.data.others

data class Page<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val size: Int,
    val number: Int
)
