package com.app.patlivecare.profile.repository

import com.app.patlivecare.network.WebService
import com.app.patlivecare.profile.model.AlterProfilePicResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody

class MyProfileRepository (val webService: WebService) {

    @ExperimentalCoroutinesApi
    suspend fun alterProfilePic(headerMap: Map<String, String>, multipart: MultipartBody.Part): Flow<AlterProfilePicResponse> {
        return flow{
            val result = webService.alterProfilePic(headerMap, multipart)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }
}