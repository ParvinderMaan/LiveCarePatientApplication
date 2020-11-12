package com.app.patlivecare

import android.app.Application
import com.app.patlivecare.network.ServiceGenerator
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.network.WebService
import com.facebook.FacebookSdk
import com.jakewharton.threetenabp.AndroidThreeTen


class LiveCareApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this) // Without this ThreeTenABP cannot work properly
        FacebookSdk.sdkInitialize(this)

    }

    // @use DI
    fun getWebServiceInstance(): WebService {
        return getRetrofitInstance().createService(WebService::class.java)
    }

    // @use DI
    fun getRetrofitInstance(): ServiceGenerator {
        return ServiceGenerator.getInstance(this)
    }

    // @use DI
    fun getSharedPrefInstance(): SharedPrefHelper {
        return SharedPrefHelper.getInstance(this)
    }

    // @use DI
    fun getEventBusInstance(): LiveCareEventBus {
        return LiveCareEventBus.getInstance(this)
    }

}
