package com.app.patlivecare.consult.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.consult.model.AppointmentInfo
import com.app.patlivecare.consult.repository.PastAppointmentRepository
import com.app.patlivecare.network.WebService
import java.util.HashMap

class PastAppointmentViewModel (application: Application) : AndroidViewModel(application) {
    var headers: HashMap<String, String>
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    private var config: PagedList.Config
    var isListEmpty: MutableLiveData<Boolean>
    private lateinit var itemDataSourceFactory: PastAppointmentRepository.PastAppointmentDataSourceFactory
    private lateinit var liveDataSource: LiveData<PastAppointmentRepository>
    lateinit var userPagedList: LiveData<PagedList<AppointmentInfo>>
    var lstOfDoctor: LiveData<List<AppointmentInfo>>

    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        isListEmpty = MutableLiveData()
        lstOfDoctor = MutableLiveData()
        headers = HashMap()
        config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(10)
            .setPageSize(10)
            .build()
    }


    fun fetchPastAppointments() {
        itemDataSourceFactory = PastAppointmentRepository.PastAppointmentDataSourceFactory(webService, headers)
        liveDataSource = itemDataSourceFactory.userLiveDataSource
        userPagedList = LivePagedListBuilder(itemDataSourceFactory, config)
            .setBoundaryCallback(object : PagedList.BoundaryCallback<AppointmentInfo>() {
                override fun onZeroItemsLoaded() {
                    super.onZeroItemsLoaded()
                    // Handle empty initial load here
                    isListEmpty.value = true
                    isLoading.value = false
                }

                override fun onItemAtEndLoaded(itemAtEnd: AppointmentInfo) {
                    super.onItemAtEndLoaded(itemAtEnd)
                    // Here you can listen to last item on list

                }

                override fun onItemAtFrontLoaded(itemAtFront: AppointmentInfo) {
                    super.onItemAtFrontLoaded(itemAtFront)
                    // Here you can listen to first item on list
                    isLoading.value = false
                }
            })
            .build()

    }

}