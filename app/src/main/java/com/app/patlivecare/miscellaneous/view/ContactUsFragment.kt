package com.app.patlivecare.miscellaneous.view

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.R
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.helper.Validator
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.base.isUserInteractionEnabled
import com.app.patlivecare.miscellaneous.viewmodel.ContactUsViewModel
import com.app.patlivecare.network.WebHeader
import kotlinx.android.synthetic.main.fragment_contact_us.*
import kotlinx.android.synthetic.main.fragment_contact_us.progress_bar
import kotlinx.coroutines.*
import java.util.HashMap


class ContactUsFragment : BaseFragment() {
    private var mFragmentListener: HomeFragmentSelectedListener? = null
    private lateinit var headerMap: HashMap<String, String>
    private var sharedPrefs: SharedPrefHelper? = null
    private lateinit var viewModel: ContactUsViewModel

    companion object {
        fun newInstance() = ContactUsFragment()
    }

    override fun getRootView(): View {
        return cl_root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()
        if (context is HomeFragmentSelectedListener) mFragmentListener = context

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        headerMap = HashMap<String, String>()
        headerMap.put(WebHeader.KEY_CONTENT_TYPE, WebHeader.VAL_CONTENT_TYPE)
        headerMap.put(WebHeader.KEY_AUTHORIZATION,"Bearer "+accessToken)
        viewModel = ViewModelProvider(this).get(ContactUsViewModel::class.java)
        viewModel.headerMap=headerMap
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contact_us, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_header_title?.text = getString(R.string.title_contact_us)
        if(activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(toolbar_main)

        ibtn_close.setOnClickListener {
            activity?.finish()
        }

        initObserver()

        btn_send?.setOnClickListener {
             if(isFormValid()){
                 viewModel.attemptContactUs()
             }
        }

    }

    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner,
            Observer {
                if (it){
                    progress_bar?.show()
                    btn_send?.visibility=View.INVISIBLE
                }
                else {
                    progress_bar?.hide()
                    btn_send?.visibility=View.VISIBLE
                }
            })

        viewModel.isViewEnable.observe(viewLifecycleOwner,
            Observer {
                if (it) view?.isUserInteractionEnabled(false)
                else view?.isUserInteractionEnabled(true)
            })

        viewModel.resultContactUs.observe(viewLifecycleOwner, Observer {

            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.message?.let { it1 -> showSnackBar(it1,R.color.colorGreen) }
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(1200)
                        withContext(Dispatchers.Main) {
                            mFragmentListener?.popTopMostFragment()
                        }
                    }
                }
                Status.FAILURE -> it.errorMsg?.let { showSnackBar(it) }
            }
        })

    }

    private fun isFormValid(): Boolean {
        val name = edt_name.text.toString()
        val email = edt_email.text.toString()
        val phoneNumber = edt_mobile.text.toString()
        val message = edt_message.text.toString()

        if (TextUtils.isEmpty(name)
            || TextUtils.isEmpty(email)
            || TextUtils.isEmpty(phoneNumber)
            || TextUtils.isEmpty(message)){

            showSnackBar(getString(R.string.alert_please_complete_required_info))
            return false



        }

        if(!Validator.isEmailValid(email)){
            showSnackBar(getString(R.string.alert_email_is_not_valid))
            return false
        }

        viewModel.contactUsRequest.name=name
        viewModel.contactUsRequest.email=email
        viewModel.contactUsRequest.mobileNo=phoneNumber
        viewModel.contactUsRequest.message=message
        return true
    }
}