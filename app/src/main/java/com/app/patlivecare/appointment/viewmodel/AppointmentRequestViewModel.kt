package com.app.patlivecare.appointment.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.patlivecare.extra.GeneralResponse
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.appointment.repository.AppointmentRequestRepository
import com.app.patlivecare.extra.WebResponse
import com.app.patlivecare.helper.ErrorHandler
import com.app.patlivecare.network.WebService
import com.google.gson.JsonObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AppointmentRequestViewModel(application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    var isViewEnable: MutableLiveData<Boolean>
    var resultAppointmentReq: MutableLiveData<WebResponse<GeneralResponse>>
    var headerMap: Map<String, String>
    var repository: AppointmentRequestRepository
    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        isViewEnable= MutableLiveData()
        headerMap = HashMap()
        resultAppointmentReq= MutableLiveData()
        repository= AppointmentRequestRepository(webService)
    }

    @ExperimentalCoroutinesApi
    fun attemptAppointmentRequest(doctorId:String,timeSlotId:Int) {
        viewModelScope.launch {
            val json=JsonObject()
            json.addProperty("timeSlotId",timeSlotId)
            json.addProperty("doctorId",doctorId)
            repository.attemptAppointmentRequest(headerMap,json)
                .onStart {
                    /* emit loading state */
                    isLoading.value=true
                    isViewEnable.value=false
                }
                .onCompletion {
                    isLoading.value=false
                 //   isViewEnable.value=true
                }
                .catch { exception ->
                    /* emit error state */
                    val errorMsg: String? = ErrorHandler.reportError(exception)
                    resultAppointmentReq.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {
                    if(it.status)resultAppointmentReq.value= WebResponse(Status.SUCCESS, it, null)
                    else resultAppointmentReq.value= WebResponse(Status.FAILURE, it, it.message)
                }
        }
    }


}