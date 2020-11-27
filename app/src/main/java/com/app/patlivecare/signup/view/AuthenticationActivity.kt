package com.app.patlivecare.signup.view


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.patlivecare.R
import com.app.patlivecare.helper.Common
import com.app.patlivecare.signin.view.SignInFragment
import kotlinx.android.synthetic.main.activity_authentication.*


class AuthenticationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_authentication)


        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.fl_container, SignInFragment.newInstance())
                addToBackStack(null)
                commit()
            }

        supportFragmentManager.addOnBackStackChangedListener {
            val currentBackStackFragment = supportFragmentManager.findFragmentById(R.id.fl_container)
            if(currentBackStackFragment is SignInFragment){
                tv_title?.text = getString(R.string.title_sign_in)
            }
            if(currentBackStackFragment is SignUpFragment){
                tv_title?.text = getString(R.string.title_sign_up)
            }
         }
        // print facebook key..
        Common.printKeyHash(packageManager)
    }

    public fun  showSignUpFragment(){
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.fl_container, SignUpFragment.newInstance())
                addToBackStack(null)
                commit()
            }

    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount>1){
            supportFragmentManager.popBackStack()
        }else{
           finish()
        }
    }

    fun hideSignUpFragment() {
        supportFragmentManager.popBackStack()
    }


}