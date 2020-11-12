package com.app.patlivecare.home.repository

import com.app.patlivecare.doctor.model.SpecialityResponse
import com.app.patlivecare.home.model.MergeResponse
import com.app.patlivecare.home.model.PatientInfoResponse
import com.app.patlivecare.network.WebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.zip

class HomeRepository (val webService: WebService) {

    @ExperimentalCoroutinesApi
    suspend fun fetchSpeciality(token: Map<String, String>): Flow<SpecialityResponse> {
        return flow {
            val result = webService.fetchSpeciality(token)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }

    @ExperimentalCoroutinesApi
    suspend fun fetchPatientInfo(token: Map<String, String>): Flow<PatientInfoResponse> {
        return flow {
            val result = webService.fetchPatientInfo(token)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }


    /*
     oneApi().zip(twoApi()) { oneApiResult, twoApiResult ->
                return@zip oneApiResult + twoApiResult
            }.zip(threeApi()){
               prevApiResult, threeApiResult ->
                return@zip prevApiResult + threeApiResult
            }.flowOn(Dispatchers.IO)
     */

    @ExperimentalCoroutinesApi
    suspend fun fetchDashboardInfo(token: Map<String, String>): Flow<MergeResponse> {
        var result: MergeResponse
        val flowOne = flow {
            emit(webService.fetchTopDoctor(token,1))
        }
        val flowTwo = flow {
            emit(webService.fetchSpeciality(token))
        }
        return flowOne.zip(flowTwo) { oneApiResult, twoApiResult ->
            if (oneApiResult.status && twoApiResult.status) {
                result = MergeResponse(
                    twoApiResult.code,
                    twoApiResult.message,
                    twoApiResult.status,
                    twoApiResult.data.listOfSpeciality,
                    oneApiResult.data.dataList
                )
            } else {
                result = MergeResponse(
                    twoApiResult.code,
                    twoApiResult.message,
                    twoApiResult.status && oneApiResult.status,
                    twoApiResult.data.listOfSpeciality,
                    oneApiResult.data.dataList
                )
            }

            return@zip result
        }.flowOn(Dispatchers.IO)
    }
}



    /*

     flowOne.zip(flowTwo) { oneApiResult, twoApiResult ->
            return@zip oneApiResult + twoApiResult
        }.zip(threeApi()){
                prevApiResult, threeApiResult ->
            return@zip prevApiResult + threeApiResult
        }.flowOn(Dispatchers.IO)
     */
