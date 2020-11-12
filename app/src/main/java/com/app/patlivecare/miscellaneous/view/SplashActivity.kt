package com.app.patlivecare.miscellaneous.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.R
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.home.view.HomeActivity
import com.app.patlivecare.signup.view.AuthenticationActivity
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {
    private var DELAY_INTERVAL=3000L
    private var sharedPrefs: SharedPrefHelper? =null
    private var isUserSignIn: Boolean? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    //    Log.e("action -->",intent.action.toString()) //: android.intent.action.MAIN  print
      /*
        for (name in intent.categories) {
            Log.e("category -->",name.toString()) //: android.intent.category.LAUNCHER  print
        }
       */
        sharedPrefs = (applicationContext as LiveCareApplication).getSharedPrefInstance()
        isUserSignIn=sharedPrefs?.read(SharedPrefHelper.KEY_IS_SIGN_IN,false)

        // IO,Main,Default
        CoroutineScope(Dispatchers.Default).launch {
            delay(DELAY_INTERVAL)
            withContext(Dispatchers.Main) {
                when(isUserSignIn) {
                    true ->  {
                        val intent= Intent(this@SplashActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    false ->   {
                        val intent= Intent(this@SplashActivity, AuthenticationActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }
}