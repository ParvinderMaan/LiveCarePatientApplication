package com.app.patlivecare.consult.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.patlivecare.GeneralResponse
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.annotation.AppointmentStatus
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.appointment.model.DoctorScheduleResponse
import com.app.patlivecare.consult.model.AppointmentInfo
import com.app.patlivecare.consult.model.UpcomingAppointmentResponse
import com.app.patlivecare.consult.repository.UpcomingAppointmentRepository
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

class UpcomingAppointmentViewModel(application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    var resultAppointmentConfirmed: MutableLiveData<WebResponse<UpcomingAppointmentResponse>>
    var resultAppointmentPending: MutableLiveData<WebResponse<UpcomingAppointmentResponse>>
    var resultPendingAppnmtCancel: MutableLiveData<WebResponse<GeneralResponse>>
    var resultConfirmAppnmtCancel: MutableLiveData<WebResponse<GeneralResponse>>
    var headerMap: Map<String, String>
    var lstOfConfirmedAppmnt: MutableLiveData<List<AppointmentInfo>>
    var lstOfPendingAppmnt: MutableLiveData<List<AppointmentInfo>>
    var repository: UpcomingAppointmentRepository
    var itemToBeDeleted: Int = -1
    var showWhichView: MutableLiveData<Int>

    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        headerMap = HashMap()
        resultAppointmentConfirmed = MutableLiveData()
        resultAppointmentPending = MutableLiveData()
        resultPendingAppnmtCancel = MutableLiveData()
        resultConfirmAppnmtCancel = MutableLiveData()
        lstOfConfirmedAppmnt = MutableLiveData()
        lstOfPendingAppmnt = MutableLiveData()
        showWhichView = MutableLiveData()
        repository = UpcomingAppointmentRepository(webService)
    }

    @ExperimentalCoroutinesApi
    fun fetchUpcomingAppointment(@AppointmentStatus appointmentStatus: Int) {
        when (appointmentStatus) {
            AppointmentStatus.CONFIRMED -> {
                viewModelScope.launch {
                    repository.fetchUpcomingAppointment(headerMap, appointmentStatus)
                        .onStart {
                            /* emit loading state */
                            isLoading.value = true
                        }
                        .onCompletion {
                            isLoading.value = false
                        }
                        .catch { exception ->
                            /* emit error state */
                            val errorMsg: String? = ErrorHandler.reportError(exception)
                            resultAppointmentConfirmed.value =
                                WebResponse(Status.FAILURE, null, errorMsg)
                        }
                        .collect {
                            if (it.status) resultAppointmentConfirmed.value =
                                WebResponse(Status.SUCCESS, it, null)
                            else resultAppointmentConfirmed.value =
                                WebResponse(Status.FAILURE, it, it.message)
                        }
                }
            }
            AppointmentStatus.PENDING -> {
                viewModelScope.launch {
                    repository.fetchUpcomingAppointment(headerMap, appointmentStatus)
                        .onStart {
                            /* emit loading state */
                            isLoading.value = true
                        }
                        .onCompletion {
                            isLoading.value = false
                        }
                        .catch { exception ->
                            /* emit error state */
                            val errorMsg: String? = ErrorHandler.reportError(exception)
                            resultAppointmentPending.value =
                                WebResponse(Status.FAILURE, null, errorMsg)
                        }
                        .collect {
                            if (it.status) resultAppointmentPending.value =
                                WebResponse(Status.SUCCESS, it, null)
                            else resultAppointmentPending.value =
                                WebResponse(Status.FAILURE, it, it.message)
                        }
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun attemptCancelAppointment(appointmentId: String, @AppointmentStatus appointmentStatus: Int) {
        val json = JsonObject()
        json.addProperty("appointmentId", appointmentId)
        json.addProperty("appointmentStatus", appointmentStatus)

        when (appointmentStatus) {
            AppointmentStatus.CONFIRMED -> {
                viewModelScope.launch {
                    repository.attemptAppointmentProcess(headerMap, json)
                        .onStart {
                            /* emit loading state */
                            isLoading.value = true
                        }
                        .onCompletion {
                            isLoading.value = false
                        }
                        .catch { exception ->
                            /* emit error state */
                            val errorMsg: String? = ErrorHandler.reportError(exception)
                            resultConfirmAppnmtCancel.value =
                                WebResponse(Status.FAILURE, null, errorMsg)
                        }
                        .collect {
                            if (it.status) resultConfirmAppnmtCancel.value =
                                WebResponse(Status.SUCCESS, it, null)
                            else resultConfirmAppnmtCancel.value =
                                WebResponse(Status.FAILURE, it, it.message)
                        }
                }
            }
            AppointmentStatus.PENDING -> {
                viewModelScope.launch {
                    repository.attemptAppointmentProcess(headerMap, json)
                        .onStart {
                            /* emit loading state */
                            isLoading.value = true
                        }
                        .onCompletion {
                            isLoading.value = false
                        }
                        .catch { exception ->
                            /* emit error state */
                            val errorMsg: String? = ErrorHandler.reportError(exception)
                            resultPendingAppnmtCancel.value =
                                WebResponse(Status.FAILURE, null, errorMsg)
                        }
                        .collect {
                            if (it.status) resultPendingAppnmtCancel.value =
                                WebResponse(Status.SUCCESS, it, null)
                            else resultPendingAppnmtCancel.value =
                                WebResponse(Status.FAILURE, it, it.message)
                        }
                }


            }
        }
    }
}
