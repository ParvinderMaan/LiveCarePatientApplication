package com.app.patlivecare.rating.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.rating.model.DoctorReviewResponse
import com.app.patlivecare.extra.WebResponse
import com.app.patlivecare.helper.ErrorHandler
import com.app.patlivecare.network.WebService
import com.app.patlivecare.rating.model.UserReview
import com.app.patlivecare.rating.repository.DoctorReviewRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.HashMap

class DoctorReviewViewModel(application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    var resultDoctorReview: MutableLiveData<WebResponse<DoctorReviewResponse>>
    var headerMap: Map<String, String>
    var repository: DoctorReviewRepository
    var lstOfReview: MutableLiveData<List<UserReview>>


    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        headerMap = HashMap()
        resultDoctorReview= MutableLiveData()
        lstOfReview= MutableLiveData()
        repository= DoctorReviewRepository(webService)
    }

    @ExperimentalCoroutinesApi
    fun fetchDoctorReview(doctorId:String) {
        viewModelScope.launch {
            repository.fetchDoctorReview(headerMap,doctorId)
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
                    resultDoctorReview.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {
                    if(it.status)resultDoctorReview.value= WebResponse(Status.SUCCESS, it, null)
                    else resultDoctorReview.value= WebResponse(Status.FAILURE, it, it.message)
                }
        }
    }




}