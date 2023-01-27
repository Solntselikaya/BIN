package com.example.bin.network

import com.example.bin.network.models.CardInfo
import retrofit2.http.GET
import retrofit2.http.Path

interface APIService {

    @GET("{number}")
    suspend fun getCardInfo(@Path("number") number: String): CardInfo
}