package com.app.patlivecare.password.repository

import com.app.patlivecare.extra.GeneralResponse
import com.app.patlivecare.network.WebService
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ChangePasswordRepository(val webService: WebService) {

    @ExperimentalCoroutinesApi
    suspend fun attemptChangePassword(headers: HashMap<String, String>,jsonObject: JsonObject): Flow<GeneralResponse> {
        return flow {
            val result = webService.attemptChangePassword(headers,jsonObject)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }
}