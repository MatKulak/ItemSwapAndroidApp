package com.mateusz.itemswap.utils

import com.mateusz.itemswap.helpers.PreferencesHelper
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val preferencesHelper: PreferencesHelper) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = preferencesHelper.getJwtToken()

        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}