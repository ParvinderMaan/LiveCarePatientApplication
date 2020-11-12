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

class FindDoctorRepository(
    private var webService: WebService,
    private var headers: HashMap<String, String>,
    private var specialityId: String,
    private var queryText: String
): PageKeyedDataSource<Long, DoctorInfo>() {
    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, DoctorInfo>) {
        webService.fetchDoctor(headers,1,searchQuery = queryText,specialityId=specialityId).enqueue(object :
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
        webService.fetchDoctor(headers,params.key,searchQuery = queryText,specialityId=specialityId).enqueue(object :
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


    class FindDoctorDataSourceFactory(
        val webService: WebService,
        var headers: HashMap<String, String>,
        var specialityId: String,
        var queryText: String
    ) : DataSource.Factory<Long, DoctorInfo>() {
        val userLiveDataSource = MutableLiveData<FindDoctorRepository>()

        override fun create(): DataSource<Long, DoctorInfo> {
            val userDataSource = FindDoctorRepository(webService,headers,specialityId,queryText)
            userLiveDataSource.postValue(userDataSource)
            return userDataSource
        }




    }
}