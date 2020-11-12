package com.app.patlivecare.videocall.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.extra.WebResponse
import com.app.patlivecare.helper.ErrorHandler
import com.app.patlivecare.network.WebService
import com.app.patlivecare.videocall.model.VideoTokenResponse
import com.app.patlivecare.videocall.repository.WaitingRoomRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class WaitingRoomViewModel (application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    var resultVideoCallToken: MutableLiveData<WebResponse<VideoTokenResponse>>
    var headerMap: Map<String, String>
    var repository: WaitingRoomRepository

    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        headerMap = HashMap()
        resultVideoCallToken = MutableLiveData()
        repository = WaitingRoomRepository(webService)
    }

    @ExperimentalCoroutinesApi
    fun fetchVideoToken(appointmentId: String) {
     viewModelScope.launch {
     repository.fetchVideoToken(headerMap, appointmentId)
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
                            resultVideoCallToken.value = WebResponse(Status.FAILURE, null, errorMsg)
                        }
                        .collect {
                            if(it.status)resultVideoCallToken.value= WebResponse(Status.SUCCESS, it, null)
                            else resultVideoCallToken.value= WebResponse(Status.FAILURE, it, it.message)
                         }
                }

        }


}