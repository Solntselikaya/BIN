package com.example.bin.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class Interceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val request: Request = chain.request().newBuilder().apply {
            addHeader("Accept", "application/json")
            addHeader("Content-Type", "application/json")
        }.build()

        val response: Response?

        return try {
            response = chain.proceed(request)
            response
        } catch (e: Exception) {
            //response?.close()
            chain.proceed(request)
        }
    }
}