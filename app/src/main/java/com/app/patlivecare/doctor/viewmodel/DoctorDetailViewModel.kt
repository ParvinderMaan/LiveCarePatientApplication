package com.app.patlivecare.doctor.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.doctor.model.DoctorDetailResponse
import com.app.patlivecare.doctor.repository.DoctorDetailRepository
import com.app.patlivecare.extra.WebResponse
import com.app.patlivecare.helper.ErrorHandler
import com.app.patlivecare.network.WebService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class DoctorDetailViewModel (application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    var resultDoctorDetail: MutableLiveData<WebResponse<DoctorDetailResponse>>

    var headerMap: Map<String, String>
    var repository: DoctorDetailRepository
    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        headerMap = HashMap()
        resultDoctorDetail= MutableLiveData()
        repository= DoctorDetailRepository(webService)
    }

    @ExperimentalCoroutinesApi
    fun fetchDoctorDetails(doctorId:String) {
        viewModelScope.launch {
            repository.fetchDoctorDetails(headerMap,doctorId)
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
                    resultDoctorDetail.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {
                    if(it.status)resultDoctorDetail.value= WebResponse(Status.SUCCESS, it, null)
                    else resultDoctorDetail.value= WebResponse(Status.FAILURE, it, it.message)
                }
        }
    }


}