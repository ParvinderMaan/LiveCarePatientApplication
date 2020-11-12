package com.app.patlivecare.rating.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.patlivecare.GeneralResponse

import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.doctor.model.DoctorDetailResponse
import com.app.patlivecare.doctor.repository.DoctorDetailRepository
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

class DoctorRatingViewModel (application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    var resultFetchDoctorRating: MutableLiveData<WebResponse<DoctorRatingResponse>>
    var resultAddDoctorRating: MutableLiveData<WebResponse<GeneralResponse>>

    var headerMap: Map<String, String>
    var repository: DoctorRatingRepository
    var isOneStarSel: MutableLiveData<Boolean>
    var isTwoStarSel: MutableLiveData<Boolean>
    var isThreeStarSel: MutableLiveData<Boolean>
    var isFourStarSel: MutableLiveData<Boolean>
    var isFiveStarSel: MutableLiveData<Boolean>
    var isViewEnable: MutableLiveData<Boolean>

    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        headerMap = HashMap()
        resultAddDoctorRating= MutableLiveData()
        resultFetchDoctorRating= MutableLiveData()
        isOneStarSel= MutableLiveData()
        isTwoStarSel= MutableLiveData()
        isThreeStarSel= MutableLiveData()
        isFourStarSel= MutableLiveData()
        isFiveStarSel= MutableLiveData()
        isViewEnable= MutableLiveData()
        repository= DoctorRatingRepository(webService)

        isOneStarSel.value=false
        isTwoStarSel.value=false
        isThreeStarSel.value=false
        isFourStarSel.value=false
        isFiveStarSel.value=false

    }

    @ExperimentalCoroutinesApi
    fun fetchDoctorRating(appointmentId:String) {
        viewModelScope.launch {
            repository.fetchDoctorAppointmentRating(headerMap,appointmentId)
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
                    resultFetchDoctorRating.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {
                    if(it.status)resultFetchDoctorRating.value= WebResponse(Status.SUCCESS, it, null)
                    else resultFetchDoctorRating.value= WebResponse(Status.FAILURE, it, it.message)
                }
        }
    }


    @ExperimentalCoroutinesApi
    fun addDoctorRatingAndReview(appointmentId:String,rating:Int) {
        viewModelScope.launch {
            val json= JsonObject()
            json.addProperty("appointmentId",appointmentId)
            json.addProperty("rating",rating)
            json.addProperty("comments","")
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
                    resultAddDoctorRating.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {
                    if(it.status)resultAddDoctorRating.value= WebResponse(Status.SUCCESS, it, null)
                    else resultAddDoctorRating.value= WebResponse(Status.FAILURE, it, it.message)
                }
        }
    }
}