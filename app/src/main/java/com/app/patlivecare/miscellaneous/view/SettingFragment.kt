package com.app.patlivecare.miscellaneous.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.patlivecare.network.WebHeader
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.MinorActivity
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.dialog.SignOutDialogFragment
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.base.isUserInteractionEnabled
import com.app.patlivecare.miscellaneous.viewmodel.SettingViewModel
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.fragment_setting.progress_bar
import java.util.*

class SettingFragment : BaseFragment() {
    private var isEmailChecked: Boolean=false
    private var isSmsChecked: Boolean=false
    private lateinit var headerMap: HashMap<String, String>
    private var sharedPrefs: SharedPrefHelper? = null
    private lateinit var viewModel: SettingViewModel
    private lateinit var accessToken: String

    companion object {
        fun newInstance() = SettingFragment()
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
        accessToken= sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN,"").toString()
         headerMap = HashMap<String, String>()
        headerMap.put(WebHeader.KEY_CONTENT_TYPE, WebHeader.VAL_CONTENT_TYPE)
        headerMap.put(WebHeader.KEY_AUTHORIZATION,"Bearer "+accessToken)
        viewModel = ViewModelProvider(this).get(SettingViewModel::class.java)
        viewModel.headerMap=headerMap
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()



        when (sharedPrefs?.read(SharedPrefHelper.KEY_EMAIL_NOTIFICATION,true)) {
            false -> switch_email_notification?.isChecked = false
            true -> switch_email_notification?.isChecked = true
        }

        when (sharedPrefs?.read(SharedPrefHelper.KEY_SMS_NOTIFICATION,true)) {
            false -> switch_sms_notification?.isChecked = false
            true -> switch_sms_notification?.isChecked = true
        }

        when (sharedPrefs?.read(SharedPrefHelper.KEY_IS_SOCIAL_SIGN_IN,false)) {
            false ->{
                ibtn_change_password?.isEnabled = true
                tv_change_password?.isEnabled = true
            }
            true -> {
            ibtn_change_password?.isEnabled = false
            tv_change_password?.isEnabled = false
            }
        }
        initListener()
    }

    private fun initListener() {
        tv_sign_out?.setOnClickListener {
//            sharedPrefs?.write(SharedPrefHelper.KEY_IS_SIGN_IN, false)
//            val intent= Intent(activity, SplashActivity::class.java)
//            startActivity(intent)
//            activity?.finish()

            val fragment: SignOutDialogFragment = SignOutDialogFragment.newInstance()
            fragment.setOnSignOutDialogListener(object:SignOutDialogFragment.SignOutDialogListener{
                override fun onClickYes() {
                    fragment.dismiss()
                    viewModel.attemptSignOut()
                }

                override fun onClickNo() {
                    fragment.dismiss()
                }

            })
            fragment.show(childFragmentManager, "TAG")
        }

        ibtn_change_password?.setOnClickListener {
            val intent = Intent(activity, MinorActivity::class.java)
            intent.putExtra("fragment_type", FragmentType.CHANGE_PASSWORD_FRAGMENT)
            startActivity(intent)
        }

        ibtn_privacy_policy?.setOnClickListener {
            val intent = Intent(activity, MinorActivity::class.java)
            intent.putExtra("fragment_type", FragmentType.PRIVACY_POLICY_FRAGMENT)
            startActivity(intent)

        }

        ibtn_terms_and_conditions?.setOnClickListener {
            val intent = Intent(activity, MinorActivity::class.java)
            intent.putExtra("fragment_type", FragmentType.TERM_AND_CONDITION_FRAGMENT)
            startActivity(intent)
        }

        ibtn_contact_us?.setOnClickListener {
            val intent = Intent(activity, MinorActivity::class.java)
            intent.putExtra("fragment_type", FragmentType.CONTACT_US_FRAGMENT)
            startActivity(intent)
        }

        switch_email_notification?.setOnCheckedChangeListener(object: CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
                   isEmailChecked=isChecked
                   viewModel.alterEmailNotificationService(headerMap,isEmailChecked)

            }
        });

        switch_sms_notification?.setOnCheckedChangeListener(object: CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
                isSmsChecked=isChecked
                viewModel.alterSmsNotificationService(headerMap,isSmsChecked)
            }
        });
    }

    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner,
            Observer {
                if (it)progress_bar?.visibility = View.VISIBLE
                else progress_bar?.visibility = View.INVISIBLE
            })

        viewModel.isViewEnable.observe(viewLifecycleOwner,
            Observer {
                if (it) view?.isUserInteractionEnabled(true)
                else view?.isUserInteractionEnabled(false)
            })

        viewModel.resultantSignOut.observe(viewLifecycleOwner, Observer {

            when (it.status) {
                Status.SUCCESS -> {
                    sharedPrefs?.builder()?.write(SharedPrefHelper.KEY_IS_SIGN_IN, false)?.build()
                    val intent = Intent(activity, SplashActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                Status.FAILURE -> it.errorMsg?.let { showSnackBar(it) }
            }
        })

        viewModel.resultantEmailService.observe(viewLifecycleOwner, Observer {

            when (it.status) {
                Status.SUCCESS -> {
                    sharedPrefs?.builder()?.write(SharedPrefHelper.KEY_EMAIL_NOTIFICATION,isEmailChecked)
                        ?.build()
                }
                Status.FAILURE -> {
                    it.errorMsg?.let {
                        sharedPrefs?.builder()?.write(SharedPrefHelper.KEY_EMAIL_NOTIFICATION,isEmailChecked)
                            ?.build()
                        showSnackBar(it) }
                }
            }
        })

        viewModel.resultantSmsService.observe(viewLifecycleOwner, Observer {

            when (it.status) {
                Status.SUCCESS -> {
                    sharedPrefs?.builder()?.write(SharedPrefHelper.KEY_SMS_NOTIFICATION,isSmsChecked)
                        ?.build()
                }
                Status.FAILURE -> {
                    it.errorMsg?.let {
                        sharedPrefs?.builder()?.write(SharedPrefHelper.KEY_SMS_NOTIFICATION,isSmsChecked)
                            ?.build()
                        showSnackBar(it)
                    }
                }
            }
        })
    }

}