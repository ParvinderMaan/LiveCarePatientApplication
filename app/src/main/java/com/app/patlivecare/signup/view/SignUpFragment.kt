package com.app.patlivecare.signup.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.MinorActivity
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.signup.model.SignUpRequest
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : BaseFragment(){
    private var viewPagerAdapter: SignUpViewPagerAdapter?=null
    private var sharedPrefs: SharedPrefHelper? =null
    private val DEVICE_TYPE = "android" // ios
    lateinit var signUpRequest:SignUpRequest

    companion object {
        fun newInstance() = SignUpFragment()
    }

    override fun getRootView(): View {
        return cl_root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         viewPagerAdapter = SignUpViewPagerAdapter(childFragmentManager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
         signUpRequest= SignUpRequest()
         signUpRequest.deviceType=DEVICE_TYPE
         signUpRequest.deviceToken="12345"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view_pager?.adapter = viewPagerAdapter
        view_pager?.apply {
            offscreenPageLimit = 6 // in order to reserve  view  's  !!
            setPagingEnabled(false) // non swipe-able
        }

    }

    fun showPreviousFragment(){
         view_pager?.currentItem = view_pager.currentItem.minus(1)
     }

     fun showNextFragment(){
        view_pager?.currentItem = view_pager.currentItem.plus(1)
    }

     fun showFragment(@FragmentType fragment: String) {
        when (fragment) {
            FragmentType.PRIVACY_POLICY_FRAGMENT -> {
                val intent=Intent(activity,MinorActivity::class.java)
                intent.putExtra("fragment_type",FragmentType.PRIVACY_POLICY_FRAGMENT)
                startActivity(intent)
            }
            FragmentType.TERM_AND_CONDITION_FRAGMENT -> {
                val intent=Intent(activity,MinorActivity::class.java)
                intent.putExtra("fragment_type",FragmentType.TERM_AND_CONDITION_FRAGMENT)
                startActivity(intent)
            }
        }
    }

    fun hideSignUpFragment(){
        (activity as AuthenticationActivity).hideSignUpFragment()

    }





}