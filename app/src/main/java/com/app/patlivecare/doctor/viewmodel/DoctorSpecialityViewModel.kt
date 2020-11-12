package com.app.patlivecare.doctor.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.patlivecare.GeneralResponse
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.annotation.Status
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

class DoctorSpecialityViewModel (application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    var resultantSpeciality: MutableLiveData<WebResponse<SpecialityResponse>>
    var headerMap: Map<String, String>
    var lstOfSpeciality: MutableLiveData<List<SpecialityInfo>>
    var repository: HomeRepository
    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        headerMap = HashMap()
        resultantSpeciality= MutableLiveData()
        lstOfSpeciality= MutableLiveData()
        repository= HomeRepository(webService)
    }

    @ExperimentalCoroutinesApi
    fun fetchSpeciality() {
        viewModelScope.launch {
            repository.fetchSpeciality(headerMap)
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
                    resultantSpeciality.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {
                    if(it.status)resultantSpeciality.value= WebResponse(Status.SUCCESS, it, null)
                    else resultantSpeciality.value= WebResponse(Status.FAILURE, it, it.message)
                }
        }
    }


}