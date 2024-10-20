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
}