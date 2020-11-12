package com.app.patlivecare.appointment.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.appointment.model.DoctorScheduleResponse
import com.app.patlivecare.appointment.model.DoctorTimeSlotResponse
import com.app.patlivecare.appointment.model.TimeSlotInfo
import com.app.patlivecare.appointment.repository.TimeSlotRepository
import com.app.patlivecare.extra.WebResponse
import com.app.patlivecare.helper.ErrorHandler
import com.app.patlivecare.helper.SingleLiveEvent
import com.app.patlivecare.network.WebService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class TimeSlotViewModel(application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    var resultDoctorTimeSlot: SingleLiveEvent<WebResponse<DoctorTimeSlotResponse>>
    var headerMap: Map<String, String>
    var lstOfTimeSlots: MutableLiveData<List<TimeSlotInfo>>
    var repository: TimeSlotRepository
    var showSlots:MutableLiveData<Int>

    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        headerMap = HashMap()
        resultDoctorTimeSlot= SingleLiveEvent()
        lstOfTimeSlots= MutableLiveData()
        showSlots= MutableLiveData()
        repository= TimeSlotRepository(webService)
    }

    @ExperimentalCoroutinesApi
    fun fetchDoctorTimeSlot(doctorId:String,date:String) {
        viewModelScope.launch {
            repository.fetchDoctorTimeSlot(headerMap,doctorId,date)
                .onStart {
                    /* emit loading state */
                    isLoading.value=true
                }
                .onCompletion {
                    isLoading.value=false
                }
                .catch { exception ->
                    /* emit error state */
                    val errorMsg: String? = ErrorHandler.reportError(exception)
                    resultDoctorTimeSlot.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {
                    if(it.status)resultDoctorTimeSlot.value= WebResponse(Status.SUCCESS, it, null)
                    else resultDoctorTimeSlot.value= WebResponse(Status.FAILURE, it, it.message)
                }
        }
    }

    @ExperimentalCoroutinesApi
    fun fetchDoctorTimeSlotNew(doctorId:String,prevDate:String,currDate:String,nextDate:String) {
        viewModelScope.launch {
            repository.fetchDoctorTimeSlotNew(headerMap,doctorId,prevDate,currDate,nextDate)
                .onStart {
                    /* emit loading state */
                    isLoading.value=true
                }
                .onCompletion {
                    isLoading.value=false
                }
                .catch { exception ->
                    /* emit error state */
                    val errorMsg: String? = ErrorHandler.reportError(exception)
                    resultDoctorTimeSlot.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {
                    if(it.status)resultDoctorTimeSlot.value= WebResponse(Status.SUCCESS, it, null)
                    else resultDoctorTimeSlot.value= WebResponse(Status.FAILURE, it, it.message)
                }
        }
    }

}