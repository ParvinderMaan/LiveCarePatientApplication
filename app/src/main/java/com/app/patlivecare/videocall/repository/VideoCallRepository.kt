package com.app.patlivecare.videocall.repository

import com.app.patlivecare.network.WebService

class VideoCallRepository (var webService: WebService) {

//    @ExperimentalCoroutinesApi
//    suspend fun fetchVideoToken(token: Map<String, String>, appointmentId:String): Flow<GeneralResponse> {
//        return flow {
//            emit(webService.fetchVideoToken(token, appointmentId))
//        }.flowOn(Dispatchers.IO)
//    }

}