package com.app.patlivecare.password.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.R
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.annotation.Verification
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.base.BaseTextWatcher
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.base.isUserInteractionEnabled
import com.app.patlivecare.network.WebHeader
import com.app.patlivecare.password.viewmodel.VerificationViewModel
import kotlinx.android.synthetic.main.fragment_verification.*
import kotlinx.coroutines.*
import java.util.*


class VerificationFragment : BaseFragment() {
    private var verificationType: Int = 0
    private var headerMap: HashMap<String, String>? = null
    private lateinit var viewModel: VerificationViewModel
    private var usrOtp: String = ""
    private var usrEmail: String = ""
    private var usrPhone: String = ""
    private var accessToken: String = ""
    private var mFragmentListener: HomeFragmentSelectedListener? = null
    private var sharedPrefs: SharedPrefHelper? = null

    companion object {

        fun newInstance(payload: Any?): VerificationFragment {
            val fragment = VerificationFragment()
            val bundle = Bundle()
            if (payload is Intent) bundle.putInt(
                "KEY",
                payload.getIntExtra("verify", Verification.EMAIL)
            )
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getRootView(): View {
        return cl_root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeFragmentSelectedListener) mFragmentListener =
            context as HomeFragmentSelectedListener
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        headerMap = HashMap<String, String>()
        headerMap!![WebHeader.KEY_CONTENT_TYPE] = WebHeader.VAL_CONTENT_TYPE
        headerMap!![WebHeader.KEY_AUTHORIZATION] = "Bearer " + accessToken

        arguments?.let {
            verificationType = it.getInt("KEY")
           when(verificationType){
             Verification.EMAIL  ->{
                 usrEmail = sharedPrefs?.read(SharedPrefHelper.KEY_EMAIL, "").toString()

             }
             Verification.PHONE  ->  {
                usrPhone = sharedPrefs?.read(SharedPrefHelper.KEY_PHONE, "").toString()

             }
           }

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_verification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(VerificationViewModel::class.java)
        super.onViewCreated(view, savedInstanceState)

        initListener()
        initEditorListener()
        when(verificationType){
            Verification.EMAIL  ->{
                usrEmail.let { tv_title_ii?.text = it
                    viewModel.attemptSendOtpAtEmail(usrEmail)
                    tv_title_iii?.text =getString(R.string.title_to_verify_your_email)
                }


            }
            Verification.PHONE  ->  {
                usrPhone.let { tv_title_ii?.text = it.replace("|"," ")
                    headerMap?.let { it1 -> viewModel.attemptSendOtpAtPhone(it1,usrPhone) }
                    tv_title_iii?.text =getString(R.string.title_to_verify_your_phone)
                }
            }
        }

        initObserver()
    }

    private fun initEditorListener() {
        edt_otp_one?.addTextChangedListener(object : BaseTextWatcher() {
            override fun onTextChanged(
                start: Int,
                before: Int,
                count: Int,
                s: CharSequence?
            ) {
                if (count == 1) edt_otp_two?.requestFocus()
            }
        })

        edt_otp_two?.addTextChangedListener(object : BaseTextWatcher() {
            override fun onTextChanged(
                start: Int,
                before: Int,
                count: Int,
                s: CharSequence?
            ) {
                if (count == 1) edt_otp_three?.requestFocus()
                if (count == 0) edt_otp_one?.requestFocus()
            }
        })

        edt_otp_three?.addTextChangedListener(object : BaseTextWatcher() {
            override fun onTextChanged(
                start: Int,
                before: Int,
                count: Int,
                s: CharSequence?
            ) {
                if (count == 1) edt_otp_four?.requestFocus()
                if (count == 0) edt_otp_two?.requestFocus()
            }
        })

        edt_otp_four?.addTextChangedListener(object : BaseTextWatcher() {
            override fun onTextChanged(
                start: Int,
                before: Int,
                count: Int,
                s: CharSequence?
            ) {
                if (count == 0) edt_otp_three.requestFocus()
//                if (count == 1)
            }
        })


        edt_otp_one?.setOnEditorActionListener(OnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                edt_otp_two?.requestFocus()
            }
            false
        })

        edt_otp_two?.setOnEditorActionListener(OnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_PREVIOUS) {
                edt_otp_one?.requestFocus()
            }
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                edt_otp_three?.requestFocus()
            }
            false
        })

        edt_otp_three?.setOnEditorActionListener(OnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_PREVIOUS) {
                edt_otp_two?.requestFocus()
            }
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                edt_otp_four?.requestFocus()
            }
            false
        })

        edt_otp_four?.setOnEditorActionListener(OnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_PREVIOUS) {
                edt_otp_three?.requestFocus()
            }
            if (actionId == EditorInfo.IME_ACTION_GO) {
                hideKeyboard(edt_otp_four)
                when(verificationType){
                    Verification.EMAIL  ->{
                        attemptEmailVerification()
                    }
                    Verification.PHONE  ->{
                        attemptPhoneVerification()
                    }

                }
            }
            false
        })


    }

    @ExperimentalCoroutinesApi
    private fun attemptEmailVerification() {
        if (isFormValid()) {
            edt_otp_one?.clearFocus()
            edt_otp_two?.clearFocus()
            edt_otp_three?.clearFocus()
            edt_otp_four?.clearFocus()
            headerMap?.let { viewModel.attemptVerifyEmail(it, usrEmail, usrOtp) }
        }
    }

    private fun initListener() {
        btn_continue?.setOnClickListener {

            when(verificationType){
                Verification.EMAIL  ->{
                    attemptEmailVerification()
                }
                Verification.PHONE  ->{
                    attemptPhoneVerification()
                }

            }
        }

        tv_back?.setOnClickListener {
            mFragmentListener?.popTopMostFragment()
        }

        tv_title_resend_otp?.setOnClickListener {

            when(verificationType){
                Verification.EMAIL  ->{
                    viewModel.attemptSendOtpAtEmail(usrEmail)
                }
                Verification.PHONE  ->{
                    headerMap?.let { it1 -> viewModel.attemptSendOtpAtPhone(it1,usrPhone) }
                }

            }
        }

    }

    private fun attemptPhoneVerification() {
        if (isFormValid()) {
            edt_otp_one?.clearFocus()
            edt_otp_two?.clearFocus()
            edt_otp_three?.clearFocus()
            edt_otp_four?.clearFocus()
            headerMap?.let {  viewModel.attemptVerifyPhone(it,usrOtp) }
        }

    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun isFormValid(): Boolean {
        val a: String = edt_otp_one.text.toString()
        val b: String = edt_otp_two.text.toString()
        val c: String = edt_otp_three.text.toString()
        val d: String = edt_otp_four.text.toString()
        if (TextUtils.isEmpty(a) && TextUtils.isEmpty(b) && TextUtils.isEmpty(c) && TextUtils.isEmpty(
                d
            )
        ) {
            showSnackBar(getString(R.string.alert_verify_otp))
            return false
        }
        // please dont do it above !!!
        usrOtp = a + b + c + d
        return true
    }

    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner,
            Observer {
                if (it) {
                    progress_bar?.visibility = View.VISIBLE
                    btn_continue?.visibility = View.INVISIBLE
                    view?.isUserInteractionEnabled(false)

                } else {
                    progress_bar?.visibility = View.INVISIBLE
                    btn_continue?.visibility = View.VISIBLE
                    view?.isUserInteractionEnabled(true)

                }
            })



        viewModel.resultantSendOtpAtEmail.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> showSnackBar(it.data?.message.toString(), R.color.colorGreen)
                Status.FAILURE -> showSnackBar(it.errorMsg.toString())
            }
        })

        viewModel.resultantSendOtpAtPhone.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> showSnackBar(it.data?.message.toString(), R.color.colorGreen)
                Status.FAILURE -> showSnackBar(it.errorMsg.toString())
            }
        })

        viewModel.resultantVerifyEmail.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    showSnackBar(it.data?.message.toString(), R.color.colorGreen)
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(1200)
                        withContext(Dispatchers.Main) {
                            mFragmentListener?.popTopMostFragment()
                            // update ui ??????????????????????????????????
                            val intent = Intent("event_refresh_ui")
                            LocalBroadcastManager.getInstance(activity as Context)
                                .sendBroadcast(intent)
                        }
                    }
                }
                Status.FAILURE -> showSnackBar(it.errorMsg.toString())
            }
        })

        viewModel.resultantVerifyPhone.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    showSnackBar(it.data?.message.toString(), R.color.colorGreen)
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(1200)
                        withContext(Dispatchers.Main) {
                            mFragmentListener?.popTopMostFragment()
                            // update ui ??????????????????????????????????
                            val intent = Intent("event_refresh_ui")
                            LocalBroadcastManager.getInstance(activity as Context)
                                .sendBroadcast(intent)
                        }
                    }
                }
                Status.FAILURE -> showSnackBar(it.errorMsg.toString())
            }
        })

    }

}