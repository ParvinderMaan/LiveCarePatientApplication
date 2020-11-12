package com.app.patlivecare.profile.viewmodel

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
import com.app.patlivecare.profile.model.AlterProfilePicResponse
import com.app.patlivecare.profile.model.BasicInfoResponse
import com.app.patlivecare.profile.repository.MyProfileRepository
import com.app.patlivecare.profile.repository.ProfileBasicRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class MyProfileViewModel(application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    var resultAlterProfilePic: MutableLiveData<WebResponse<AlterProfilePicResponse>>
    var myProfileRepository: MyProfileRepository

    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        resultAlterProfilePic = MutableLiveData()
        myProfileRepository = MyProfileRepository(webService)
    }

    @ExperimentalCoroutinesApi
    fun alterProfilePic(headerMap: Map<String, String>, multipart: MultipartBody.Part){
        viewModelScope.launch {
            myProfileRepository.alterProfilePic(headerMap,multipart)
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
                    resultAlterProfilePic.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {
                    if(it.status)
                    resultAlterProfilePic.value= WebResponse(Status.SUCCESS, it, null)
                    else
                    resultAlterProfilePic.value= WebResponse(Status.FAILURE, null, it.message)
                }
        }
    }



}