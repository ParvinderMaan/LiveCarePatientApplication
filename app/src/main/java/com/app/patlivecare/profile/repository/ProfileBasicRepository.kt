package com.app.patlivecare.profile.repository

import com.app.patlivecare.extra.GeneralResponse
import com.app.patlivecare.network.WebService
import com.app.patlivecare.profile.model.BasicInfoResponse
import com.app.patlivecare.profile.model.BasicProfileRequest
import com.app.patlivecare.profile.model.CountryInfoResponse
import com.app.patlivecare.profile.model.StateInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ProfileBasicRepository (val webService: WebService) {

    @ExperimentalCoroutinesApi
    suspend fun fetchCountries(): Flow<CountryInfoResponse> {
        return flow{
            val result = webService.fetchCountries()
            emit(result)
        }.flowOn(Dispatchers.IO)

    }


    @ExperimentalCoroutinesApi
    suspend fun fetchStates(countryId: Int): Flow<StateInfoResponse> {
        return flow{
            val result = webService.fetchStates(countryId)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }


    @ExperimentalCoroutinesApi
    suspend fun fetchBasicProfileInfo(headerMap: Map<String, String>): Flow<BasicInfoResponse> {
        return flow{
            val result = webService.fetchBasicProfileInfo(headerMap)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }


    @ExperimentalCoroutinesApi
    suspend fun updateBasicProfileInfo(headerMap: Map<String, String>, basicProRequest: BasicProfileRequest): Flow<GeneralResponse> {
        return flow{
            val result = webService.updateBasicProfileInfo(headerMap, basicProRequest)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }


}