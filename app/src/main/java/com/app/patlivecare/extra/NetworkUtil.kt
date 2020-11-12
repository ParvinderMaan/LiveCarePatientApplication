package com.app.patlivecare.extra

import android.content.Context
import android.net.ConnectivityManager

class NetworkUtil {


    companion object {
        @JvmStatic
        fun isNetAvail(context: Context?): Boolean {
            val connectivityManager =
                context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }


    }

}