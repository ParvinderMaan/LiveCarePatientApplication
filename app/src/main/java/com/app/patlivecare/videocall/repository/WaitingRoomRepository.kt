package com.app.patlivecare.videocall.repository

import com.app.patlivecare.network.WebService
import com.app.patlivecare.videocall.model.VideoTokenResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class WaitingRoomRepository(var webService: WebService) {
    @ExperimentalCoroutinesApi
    suspend fun fetchVideoToken(token: Map<String, String>, appointmentId:String): Flow<VideoTokenResponse> {
        return flow {
            emit(webService.fetchVideoToken(token, appointmentId))
        }.flowOn(Dispatchers.IO)
    }

}