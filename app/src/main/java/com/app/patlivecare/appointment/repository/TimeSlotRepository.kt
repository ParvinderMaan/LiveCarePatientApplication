package com.app.patlivecare.appointment.repository

import com.app.patlivecare.appointment.model.DoctorScheduleResponse
import com.app.patlivecare.appointment.model.DoctorTimeSlotResponse
import com.app.patlivecare.appointment.model.TimeSlotInfo
import com.app.patlivecare.home.model.MergeResponse
import com.app.patlivecare.network.WebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.zip
import java.lang.Exception

class TimeSlotRepository (val webService: WebService) {

    @ExperimentalCoroutinesApi
    suspend fun fetchDoctorTimeSlot(token: Map<String, String>,doctorId:String,date:String): Flow<DoctorTimeSlotResponse> {
         return flow {
              emit(webService.fetchDoctorTimeSlot(token,doctorId,date))
          }
         .flowOn(Dispatchers.IO)
    }

    @ExperimentalCoroutinesApi
    suspend fun fetchDoctorTimeSlotNew(token: Map<String, String>,doctorId:String,prevDate:String,currDate:String,nextDate:String): Flow<DoctorTimeSlotResponse> {
        var result: DoctorTimeSlotResponse
        val flowOne = flow {
            emit(webService.fetchDoctorTimeSlot(token, doctorId, prevDate))
        }
        val flowTwo = flow {
            emit(webService.fetchDoctorTimeSlot(token, doctorId, currDate))
        }
        val flowThree = flow {
            emit(webService.fetchDoctorTimeSlot(token, doctorId, nextDate))
        }
        return flowOne.zip(flowTwo) { oneApiResult, twoApiResult ->

            if (oneApiResult.status && twoApiResult.status) {
                val lstAll= mutableListOf<TimeSlotInfo>()
                lstAll.addAll(oneApiResult.data)
                lstAll.addAll(twoApiResult.data)
                result = DoctorTimeSlotResponse(
                    twoApiResult.code,
                    lstAll,
                    twoApiResult.message,
                    twoApiResult.status
                )
            } else {
                result = DoctorTimeSlotResponse(
                    twoApiResult.code,
                    twoApiResult.data,
                    twoApiResult.message,
                    twoApiResult.status && oneApiResult.status
                )
            }

            return@zip result
        }.zip(flowThree) { oneApiResult, twoApiResult ->
            if (oneApiResult.status && twoApiResult.status) {
                val lstAll= mutableListOf<TimeSlotInfo>()
                lstAll.addAll(oneApiResult.data)
                lstAll.addAll(twoApiResult.data)
                result = DoctorTimeSlotResponse(
                    twoApiResult.code,
                    lstAll,
                    twoApiResult.message,
                    twoApiResult.status
                )
            }
            else {
                result = DoctorTimeSlotResponse(
                    twoApiResult.code,
                    twoApiResult.data,
                    twoApiResult.message,
                    twoApiResult.status && oneApiResult.status
                )
            }
            return@zip result
        }.flowOn(Dispatchers.IO)
    }
}

