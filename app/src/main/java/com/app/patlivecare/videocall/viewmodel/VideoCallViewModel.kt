package com.app.patlivecare.videocall.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.app.patlivecare.extra.GeneralResponse
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.extra.WebResponse
import com.app.patlivecare.network.WebService
import com.app.patlivecare.videocall.repository.VideoCallRepository

class VideoCallViewModel(application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var showProgress: MutableLiveData<Boolean>
    var isDoctorArrived: MutableLiveData<Boolean>
    val allPermGranted: MutableLiveData<Boolean>
    val isAudioPublished: MutableLiveData<Boolean>
//    var isDeviceSettingVisited: Boolean
    var resultVideoCallToken: MutableLiveData<WebResponse<GeneralResponse>>
    var headerMap: Map<String, String>
    var repository: VideoCallRepository

    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        showProgress = MutableLiveData()
        isDoctorArrived= MutableLiveData()
        isAudioPublished= MutableLiveData()
        allPermGranted= MutableLiveData()
        headerMap = HashMap()
        resultVideoCallToken = MutableLiveData()
        repository = VideoCallRepository(webService)

        // default
      //  isDeviceSettingVisited=false
        isDoctorArrived.value=false // (although call has not connected yet !)
        isAudioPublished.value=false
    }

}