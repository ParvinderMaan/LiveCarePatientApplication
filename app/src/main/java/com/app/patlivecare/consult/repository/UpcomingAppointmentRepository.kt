package com.app.patlivecare.consult.repository

import com.app.patlivecare.extra.GeneralResponse
import com.app.patlivecare.consult.model.UpcomingAppointmentResponse
import com.app.patlivecare.network.WebService
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class UpcomingAppointmentRepository (val webService: WebService) {
    @ExperimentalCoroutinesApi
    suspend fun fetchUpcomingAppointment(token: Map<String, String>,appointmentStatus:Int): Flow<UpcomingAppointmentResponse> {
        return flow {
            emit(webService.fetchUpcomingAppointment(token, appointmentStatus))
        }.flowOn(Dispatchers.IO)
    }

    @ExperimentalCoroutinesApi
    suspend fun attemptAppointmentProcess(token: Map<String, String>, inn: JsonObject): Flow<GeneralResponse> {
        return flow {
            emit(webService.attemptAppointmentProcess(token,inn))
        }.flowOn(Dispatchers.IO)
    }


//    private suspend fun getFlow(lstOfAppointments: List<AppointmentInfo>): Flow<List<AppointmentInfo>> {
//        return flow {
//            val list = lstOfAppointments.asFlow()
//                .map {
//                    val waqt = it.date.plus(" ").plus(it.slotFrom)
//                    val outFormatterI = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
//                    val dateTime = LocalDateTime.parse(waqt, outFormatterI)
//                        .atOffset(ZoneOffset.UTC)
//                        .atZoneSameInstant(ZoneId.systemDefault())
//
//
//                    val outFormatterII = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH) // 01-09-2020 ---> 1-9-2020
//                    val formattedDate: String = dateTime.format(outFormatterII)
//                    it.slotFrom=formattedDate
//
//                    val waqtt = it.date.plus(" ").plus(it.slotTo)
//                    val dateTimeZ = LocalDateTime.parse(waqtt, outFormatterI)
//                        .atOffset(ZoneOffset.UTC)
//                        .atZoneSameInstant(ZoneId.systemDefault())
//                    val formattedDateZ: String = dateTimeZ.format(outFormatterII)
//                    it.slotTo=formattedDateZ
//
//                    it.date=dateTime.format(outFormatterI)
//                    return@map it
//                }
//
//                .toList()
//
//            emit(list)
//        }
//
//    }
}