package com.app.patlivecare.consult.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.app.patlivecare.consult.model.AppointmentInfo
import com.app.patlivecare.consult.model.PastAppointmentResponse
import com.app.patlivecare.network.WebService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class PastAppointmentRepository (private var webService: WebService, private var headers: HashMap<String, String>): PageKeyedDataSource<Long, AppointmentInfo>() {
    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, AppointmentInfo>) {
        webService.fetchPastAppointment(headers,1).enqueue(object : Callback<PastAppointmentResponse> {
            override fun onResponse(call: Call<PastAppointmentResponse>, response: Response<PastAppointmentResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()
                    if (result?.data?.dataList !=null)
                        callback.onResult(result.data?.dataList,null,2)
                }
            }

            override fun onFailure(call: Call<PastAppointmentResponse>, error: Throwable) {}
        })
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, AppointmentInfo>) {
        webService.fetchPastAppointment(headers,params.key.toInt()).enqueue(object :
            Callback<PastAppointmentResponse> {
            override fun onResponse(call: Call<PastAppointmentResponse>, response: Response<PastAppointmentResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()
                    if (result?.data?.dataList!=null)
                        callback.onResult(result.data?.dataList,params.key+1)
                }
            }

            override fun onFailure(call: Call<PastAppointmentResponse>, error: Throwable) {

            }
        })
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, AppointmentInfo>) {}


    class PastAppointmentDataSourceFactory (val webService: WebService, var headers: HashMap<String, String>) : DataSource.Factory<Long, AppointmentInfo>() {
        val userLiveDataSource = MutableLiveData<PastAppointmentRepository>()

        override fun create(): DataSource<Long, AppointmentInfo> {
            val userDataSource = PastAppointmentRepository(webService,headers)
            userLiveDataSource.postValue(userDataSource)
            return userDataSource
        }
    }
}