package com.minhhop.easygolf.framework.network.retrofit

import com.minhhop.easygolf.framework.database.PreferenceManager
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(private val preferenceManager: PreferenceManager): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestTamp = chain.request().newBuilder()
                .addHeader("Content-Type","application/json")
        preferenceManager.getToken()?.apply {
            requestTamp.addHeader("Authorization",  "Bearer $this")
        }
        val request = requestTamp.build()
        return chain.proceed(request)
    }

}