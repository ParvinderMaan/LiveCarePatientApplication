package com.app.patlivecare.rating.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.patlivecare.GeneralResponse
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.extra.WebResponse
import com.app.patlivecare.helper.ErrorHandler
import com.app.patlivecare.network.WebService
import com.app.patlivecare.rating.model.DoctorRatingResponse
import com.app.patlivecare.rating.repository.DoctorRatingRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.HashMap

class AddDoctorReviewViewModel (application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    var resultAddDoctorReview: MutableLiveData<WebResponse<GeneralResponse>>
    var headerMap: Map<String, String>
    var repository: DoctorRatingRepository
    var isViewEnable: MutableLiveData<Boolean>
    var isPostButtonVisible: MutableLiveData<Boolean>
    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        isPostButtonVisible = MutableLiveData()
        headerMap = HashMap()
        resultAddDoctorReview= MutableLiveData()
        isViewEnable= MutableLiveData()
        repository= DoctorRatingRepository(webService)

        //default
        isPostButtonVisible.value=false

    }



    @ExperimentalCoroutinesApi
    fun addDoctorRatingAndReview(appointmentId:String,comment:String) {
        viewModelScope.launch {
            val json= JsonObject()
            json.addProperty("appointmentId",appointmentId)
            json.addProperty("rating",0) // avoid
            json.addProperty("comments",comment)
            repository.addDoctorRatingAndReview(headerMap,json)
                .onStart {
                    /* emit loading state */
                    isLoading.value=true
                    isViewEnable.value=false
                }
                .onCompletion {
                    isLoading.value=false
                    isViewEnable.value=true
                }
                .catch { exception ->
                    /* emit error state */
                    val errorMsg: String? = ErrorHandler.reportError(exception)
                    resultAddDoctorReview.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {
                    if(it.status)resultAddDoctorReview.value= WebResponse(Status.SUCCESS, it, null)
                    else resultAddDoctorReview.value= WebResponse(Status.FAILURE, it, it.message)
                }
        }
    }
}