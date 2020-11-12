package com.app.patlivecare.doctor.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.app.patlivecare.doctor.model.DoctorInfo
import com.app.patlivecare.doctor.model.DoctorResponse
import com.app.patlivecare.network.WebService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class TopDoctorRepository (private var webService: WebService,private var headers: HashMap<String, String>): PageKeyedDataSource<Long, DoctorInfo>() {
    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, DoctorInfo>) {
        webService.fetchTopDoctorWithPage(headers,1).enqueue(object :
            Callback<DoctorResponse> {
            override fun onResponse(call: Call<DoctorResponse>, response: Response<DoctorResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()
                    if (result?.data?.dataList!=null)
                        callback.onResult(result.data.dataList,null,2)
                }
            }

            override fun onFailure(call: Call<DoctorResponse>, error: Throwable) {}
        })
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, DoctorInfo>) {
        webService.fetchTopDoctorWithPage(headers,params.key).enqueue(object :
            Callback<DoctorResponse> {
            override fun onResponse(call: Call<DoctorResponse>, response: Response<DoctorResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()
                    if (result?.data?.dataList!=null)
                        callback.onResult(result.data.dataList,params.key+1)
                }
            }

            override fun onFailure(call: Call<DoctorResponse>, error: Throwable) {}
        })
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, DoctorInfo>) {}


    class TopDoctorDataSourceFactory (val webService: WebService, var headers: HashMap<String, String>) : DataSource.Factory<Long, DoctorInfo>() {
        val userLiveDataSource = MutableLiveData<TopDoctorRepository>()

        override fun create(): DataSource<Long, DoctorInfo> {
            val userDataSource = TopDoctorRepository(webService,headers)
            userLiveDataSource.postValue(userDataSource)
            return userDataSource
        }




    }
}