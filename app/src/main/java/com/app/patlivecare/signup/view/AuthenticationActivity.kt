package com.app.patlivecare.signup.view

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.app.patlivecare.R
import com.app.patlivecare.miscellaneous.view.TermAndConditionFragment
import com.app.patlivecare.signin.view.SignInFragment
import kotlinx.android.synthetic.main.activity_authentication.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


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
        printKeyHash()
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

    private fun printKeyHash() {
        // Add code to print out the key hash
        try {
            val info: PackageInfo = packageManager.getPackageInfo("com.app.patlivecare", PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("KeyHash:", e.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("KeyHash:", e.toString())
        }
    }
}