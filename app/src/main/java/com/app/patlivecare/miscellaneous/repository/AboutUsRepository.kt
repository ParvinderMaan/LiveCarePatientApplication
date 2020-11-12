package com.app.patlivecare.miscellaneous.repository

import com.app.patlivecare.miscellaneous.model.AppInfoResponse
import com.app.patlivecare.network.WebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AboutUsRepository(val webService: WebService) {
    @ExperimentalCoroutinesApi
    suspend fun fetchAppInfo(): Flow<AppInfoResponse> {
        return flow {
            val result = webService.fetchAppInfo()
            emit(result)
        }.flowOn(Dispatchers.IO)
    }
}