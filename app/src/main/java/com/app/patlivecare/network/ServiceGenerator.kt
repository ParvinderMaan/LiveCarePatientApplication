package com.app.patlivecare.network

import android.util.Log
import androidx.annotation.NonNull
import com.app.patlivecare.BuildConfig
import com.app.patlivecare.extra.NetworkUtil
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.base.SingletonHolder
import com.app.patlivecare.exception.NoInternetException
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


class ServiceGenerator private constructor(var application: LiveCareApplication) {
    private val DISK_CACHE_SIZE = 10 * 1024 * 1024 // 10MB
    private var retrofit: Retrofit
  //  private  var accessToken: String
    init {
     val gson = GsonBuilder().setLenient().create()
//        val gson = GsonBuilder()
//            .setPrettyPrinting()
//        .setLenient()
//            .excludeFieldsWithoutExposeAnnotation()
//            .serializeNulls()
        //    .disableHtmlEscaping()
//            .registerTypeAdapter(ActorGson::class.java, ActorGsonSerializer())
       //     .create()
        // Init using context argument
         retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(gson))
            .client(getHttpClient())
            .baseUrl(WebUrl.BASE)
            .build()

      //  accessToken= application.getSharedPrefInstance().read(SharedPrefHelper.KEY_ACCESS_TOKEN,"").toString()

    }

    companion object : SingletonHolder<ServiceGenerator, LiveCareApplication>({ ServiceGenerator(it) })





// retrofit.create(WebService::class.java)

    private fun getHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(getCache())
            .build()
            .newBuilder()
            .addInterceptor(getHttpLoggingInterceptor())
            .addInterceptor(getNetworkInterceptor())
         //   .addInterceptor(getHeaderInterceptor())
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }


    private fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        return interceptor
    }


    private fun getCache(): Cache? {
        var cache: Cache? = null
        // Install an HTTP cache in the application cache directory.
        try {
            val cacheDir = File(application.getCacheDir(), "http")
            cache = Cache(cacheDir, DISK_CACHE_SIZE.toLong())
        } catch (e: Exception) {
            Log.e(e.toString(), "Unable to install disk cache.")
        }
        return cache
    }


    private fun getNetworkInterceptor(): Interceptor {
        return object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val isConnected: Boolean = NetworkUtil.isNetAvail(application)
                if (!isConnected) {
                    throw NoInternetException() // Throwing our custom exception 'InternetException'
                }
                val builder = chain.request().newBuilder()
                return chain.proceed(builder.build())
            }
        }
    }


    private fun getHeaderInterceptor(): Interceptor {
        return object : Interceptor {
            @NonNull
            @Override
            @Throws(IOException::class)
            override fun intercept(@NonNull chain: Interceptor.Chain): Response {
                val request = chain.request().newBuilder()
                    .addHeader(WebHeader.KEY_CONTENT_TYPE, WebHeader.VAL_CONTENT_TYPE)
                    .addHeader(WebHeader.KEY_ACCEPT, WebHeader.VAL_CONTENT_TYPE)
                 //   .addHeader(WebHeader.KEY_AUTHORIZATION,accessToken)
                    .method(chain.request().method, chain.request().body)
                    .build()
                return chain.proceed(request);
            }
        };
    }

    fun <S> createService(serviceClass: Class<S>): S {
        return  retrofit.create(serviceClass)
    }
}


