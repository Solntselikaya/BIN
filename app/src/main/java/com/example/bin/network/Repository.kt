package com.example.bin.network

import com.example.bin.network.models.CardInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class Repository {

    private val api: APIService = Network.getAPIService()

    suspend fun getCardInfo(body: String): Flow<CardInfo> = flow {
        val data = api.getCardInfo(body)
        Network.cardInfo = data
        emit(data)
    }.flowOn(Dispatchers.IO)
}