package com.app.patlivecare.medicalrecord.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class ReportViewerViewModel (application: Application) : AndroidViewModel(application) {
    var isLoading: MutableLiveData<Boolean>

    init {
        isLoading = MutableLiveData()
    }


}