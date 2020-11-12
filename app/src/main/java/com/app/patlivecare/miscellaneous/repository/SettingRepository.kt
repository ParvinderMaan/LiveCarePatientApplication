package com.app.patlivecare.miscellaneous.repository

import com.app.patlivecare.GeneralResponse
import com.app.patlivecare.network.WebService
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SettingRepository(val webService: WebService) {

    @ExperimentalCoroutinesApi
    suspend fun alterNotificationService(headers: Map<String, String>,jsonObject: JsonObject): Flow<GeneralResponse> {
        return flow {
            val result = webService.alterNotificationService(headers,jsonObject)
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    @ExperimentalCoroutinesApi
    suspend fun attemptSignOut(headers: Map<String, String>): Flow<GeneralResponse> {
        return flow {
            val result = webService.attemptSignOut(headers)
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

}