package com.app.patlivecare.videocall.repository

import com.app.patlivecare.GeneralResponse
import com.app.patlivecare.network.WebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class VideoCallRepository (var webService: WebService) {

//    @ExperimentalCoroutinesApi
//    suspend fun fetchVideoToken(token: Map<String, String>, appointmentId:String): Flow<GeneralResponse> {
//        return flow {
//            emit(webService.fetchVideoToken(token, appointmentId))
//        }.flowOn(Dispatchers.IO)
//    }

}