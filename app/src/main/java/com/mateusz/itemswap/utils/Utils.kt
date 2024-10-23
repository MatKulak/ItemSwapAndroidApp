package com.mateusz.itemswap.utils

import android.util.Base64

object Utils {

    fun decodeBase64ToByteArray(base64String: String?): ByteArray? {
        return base64String?.let {
            try {
                Base64.decode(it, Base64.DEFAULT)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    fun createParams(vararg params: Pair<String, String>): Map<String, String> {
        return mapOf(*params)
    }

    fun updateParams(oldParams: Map<String, String>, newParams: Map<String, String>): Map<String, String> {
        return oldParams.plus(newParams)
    }

    fun removeParams(map: Map<String, String>, vararg keysToRemove: String): Map<String, String> {
        return map.filterKeys { it !in keysToRemove }
    }
}