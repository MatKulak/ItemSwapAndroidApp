package com.mateusz.itemswap.utils

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser

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

    fun <T> jsonToObject(json: String, clazz: Class<T>): T {
        val gson = Gson()
        return gson.fromJson(json, clazz)
    }

    fun objectToJson(obj: Any): String {
        val gson = Gson()
        return gson.toJson(obj)
    }

    fun parseStompMessageBody(stompMessage: String): String? {
        val bodyIndex = stompMessage.indexOf("\n\n") + 2
        return if (bodyIndex != -1) stompMessage.substring(bodyIndex).trimEnd('\u0000')
        else null
    }

    fun isValidJson(json: String?): Boolean {
        return try {
            val jsonParser = JsonParser()
            val jsonElement: JsonElement = jsonParser.parse(json)
            jsonElement.isJsonObject || jsonElement.isJsonArray
        } catch (e: Exception) {
            false
        }
    }
}