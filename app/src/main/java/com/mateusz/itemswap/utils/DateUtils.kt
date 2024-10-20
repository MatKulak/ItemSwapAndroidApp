package com.mateusz.itemswap.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

    fun formatDateString(dateString: String): String {
        return try {
            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
            val date: Date? = originalFormat.parse(dateString)
            val targetFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
            date?.let { targetFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }
}