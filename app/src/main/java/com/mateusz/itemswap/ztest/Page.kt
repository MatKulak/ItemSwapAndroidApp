package com.mateusz.itemswap.ztest

data class Page<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val size: Int,
    val number: Int
)
