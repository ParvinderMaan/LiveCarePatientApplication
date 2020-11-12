package com.app.patlivecare.appointment.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.appointment.model.DoctorScheduleResponse
import com.app.patlivecare.appointment.repository.AppointmentBookingRepository
import com.app.patlivecare.doctor.model.SpecialityInfo
import com.app.patlivecare.doctor.model.SpecialityResponse
import com.app.patlivecare.extra.WebResponse
import com.app.patlivecare.helper.ErrorHandler
import com.app.patlivecare.home.repository.HomeRepository
import com.app.patlivecare.network.WebService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AppointmentBookingViewModel (application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    var resultDoctorSchedule: MutableLiveData<WebResponse<DoctorScheduleResponse>>
    var headerMap: Map<String, String>
    var lstOfAvailableDates: MutableLiveData<List<DoctorScheduleResponse.AvailableDate>>
    var repository: AppointmentBookingRepository
    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        headerMap = HashMap()
        resultDoctorSchedule= MutableLiveData()
        lstOfAvailableDates= MutableLiveData()
        repository= AppointmentBookingRepository(webService)
    }

    @ExperimentalCoroutinesApi
    fun fetchDoctorSchedule(doctorId:String,dateOfMonth:String) {
        viewModelScope.launch {
            repository.fetchDoctorSchedule(headerMap,doctorId,dateOfMonth)
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
                    resultDoctorSchedule.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {
                    if(it.status)resultDoctorSchedule.value= WebResponse(Status.SUCCESS, it, null)
                    else resultDoctorSchedule.value= WebResponse(Status.FAILURE, it, it.message)
                }
        }
    }


}