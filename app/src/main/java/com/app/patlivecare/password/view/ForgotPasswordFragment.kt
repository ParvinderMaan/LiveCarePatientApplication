package com.app.patlivecare.password.view

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.patlivecare.R
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.base.BaseTextWatcher
import com.app.patlivecare.helper.Validator
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.base.isUserInteractionEnabled
import com.app.patlivecare.password.viewmodel.ForgotPasswordViewModel
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.android.synthetic.main.layout_forgot_password.*
import kotlinx.android.synthetic.main.layout_forgot_password.edt_email
import kotlinx.android.synthetic.main.layout_reset_password.*
import kotlinx.coroutines.*


class ForgotPasswordFragment : BaseFragment() {
    private lateinit var usrEmail: String
    private lateinit var viewModel: ForgotPasswordViewModel
    private lateinit var usrNewPassword: String
    private lateinit var usrConfirmNewPassword: String
    private lateinit var usrOtp: String
    private var mFragmentListener: HomeFragmentSelectedListener? = null

    companion object {
        fun newInstance() = ForgotPasswordFragment()
    }

    override fun getRootView(): View {
        return cl_root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeFragmentSelectedListener) mFragmentListener =
            context as HomeFragmentSelectedListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(ForgotPasswordViewModel::class.java)
        super.onViewCreated(view, savedInstanceState)
        initListener()
        initTextChangedListener()
        initEditorActionListener()
        initObserver()
        val into: Animation = AnimationUtils.loadAnimation(activity, android.R.anim.slide_in_left)
        view_switcher?.inAnimation = into
    }

    private fun initEditorActionListener() {
        edt_email?.setOnEditorActionListener { v, actionId, event ->
            if (actionId== EditorInfo.IME_ACTION_GO) {
                if (isFormOneValid()) {
                    viewModel.attemptForgotPassword(usrEmail)
                }

            }
            true
        }
        edt_confirm_new_password?.setOnEditorActionListener { v, actionId, event ->
            if (actionId== EditorInfo.IME_ACTION_GO) {
                if (isFormTwoValid()) {
                    viewModel.attemptResetPassword(usrEmail,usrOtp,usrNewPassword)
                }
            }
            true
        }
    }



    private fun initTextChangedListener() {
        edt_email?.addTextChangedListener(object : BaseTextWatcher() {
            override fun onTextChanged(start: Int, before: Int, count: Int, s: CharSequence?) {
                if (s.toString().isEmpty()) return

                if (Validator.isEmailValid(s.toString())) {
                    edt_email?.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_tick_green,
                        0
                    );
                } else {
                    edt_email?.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_tick_red,
                        0
                    );
                }
            }
        })

        edt_otp?.addTextChangedListener(object : BaseTextWatcher() {
                override fun onTextChanged(start: Int, before: Int, count: Int, s: CharSequence?) {
                    if (s.toString().isEmpty()) return

                    if (s.toString().length ==4) {
                        edt_otp?.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_tick_green,
                            0
                        )
                    } else {
                        edt_otp?.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_tick_red,
                            0
                        )
                    }
                }
            })

        edt_new_password?.addTextChangedListener(object : BaseTextWatcher() {
                override fun onTextChanged(start: Int, before: Int, count: Int, s: CharSequence?) {
                    if (s.toString().isEmpty()) return
                    usrNewPassword=s.toString()
                    if (s!!.length >= 6 && Validator.isUpperAndLowerCase(s.toString()) &&
                        Validator.isDigitAndSymbolCase(s.toString())){
                        edt_new_password?.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_tick_green,
                            0
                        )
                    }else{
                        edt_new_password?.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_tick_red,
                            0
                        )
                    }
                }
            })


        edt_confirm_new_password?.addTextChangedListener(object : BaseTextWatcher() {
                override fun onTextChanged(start: Int, before: Int, count: Int, s: CharSequence?) {
                    if (s.toString().isEmpty()) return
                    usrConfirmNewPassword=s.toString()
                    if(usrConfirmNewPassword.equals(usrNewPassword)){
                        edt_confirm_new_password?.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_tick_green,
                            0
                        )
                    }else{
                        edt_confirm_new_password?.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_tick_red,
                            0
                        )
                    }
                }
            })


    }

    private fun initListener() {
        btn_send?.setOnClickListener {
            if (isFormOneValid()) {
                viewModel.attemptForgotPassword(usrEmail)

            }
        }
        btn_reset_password?.setOnClickListener {
            if (isFormTwoValid()) {
                viewModel.attemptResetPassword(usrEmail,usrOtp,usrNewPassword)
            }

        }










    }

    private fun isFormOneValid(): Boolean {
         usrEmail = edt_email.text.toString()
        if (TextUtils.isEmpty(usrEmail)) {
            edt_email?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
            return false
        }
        if (usrEmail.contains(" ")) {
            edt_email?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
            return false
        }
        if (!Validator.isEmailValid(usrEmail)) {
            edt_email?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
            return false
        }

        return true
    }

    //    edt_otp
//    edt_new_password
//    edt_confirm_new_password
    private fun isFormTwoValid(): Boolean {
        var isOtpValid=true
        var isNewPasswordValid=true
        var isConfirmNewPasswordValid=true

         usrOtp = edt_otp.text.toString()
         usrNewPassword =edt_new_password.text.toString()
         usrConfirmNewPassword =edt_confirm_new_password.text.toString()
        if (TextUtils.isEmpty(usrOtp)) {
            edt_otp?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
            isOtpValid=false

        }
        if (usrOtp.contains(" ")) {
            edt_otp?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
            isOtpValid=false
        }
        if (usrOtp.length != 4) {
            {
                edt_otp?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
                isOtpValid = false
            }
        }

        if (usrNewPassword.length < 6 && !Validator.isUpperAndLowerCase(usrNewPassword) && !Validator.isDigitAndSymbolCase(usrNewPassword)) {
            edt_new_password?.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_tick_red,
                0
            )
            isNewPasswordValid = false
        }
        if (usrConfirmNewPassword.length < 6 && !Validator.isUpperAndLowerCase(usrConfirmNewPassword) && !Validator.isDigitAndSymbolCase(usrConfirmNewPassword)) {
            edt_confirm_new_password?.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_tick_red,
                0
            )
            isConfirmNewPasswordValid = false
        }
        if (usrNewPassword.length !=usrConfirmNewPassword.length) {
            edt_confirm_new_password?.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_tick_red,
                0
            )
            isConfirmNewPasswordValid = false
        }

        return isOtpValid && isNewPasswordValid && isConfirmNewPasswordValid
    }

    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner,
            Observer {
                if (it) {
                    progress_bar_i?.visibility = View.VISIBLE
                    progress_bar_ii.visibility = View.VISIBLE
                } else {
                    progress_bar_i?.visibility = View.INVISIBLE
                    progress_bar_ii.visibility = View.INVISIBLE
                }
            })

        viewModel.isViewEnable.observe(viewLifecycleOwner,
            Observer {
                if (it) {
                    btn_send?.visibility = View.VISIBLE
                    btn_reset_password?.visibility = View.VISIBLE
                    view?.isUserInteractionEnabled(true)

                } else {
                    btn_send?.visibility = View.INVISIBLE
                    btn_reset_password?.visibility = View.INVISIBLE
                    view?.isUserInteractionEnabled(false)
                }
            }
        )

        viewModel.resultantForgotPassword.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS ->{
                    if(it.data?.data?.otpCode !=null){
                        showSnackBar(it.data?.message.toString(),R.color.colorGreen)
                        view_switcher?.showNext()
                    }else{
                        showSnackBar(it.data?.message.toString(),R.color.colorGreen)
                    }

                }
                Status.FAILURE -> showSnackBar(it.errorMsg.toString())
            }
        })

        viewModel.resultantResetPassword.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS ->{
                    showSnackBar(it.data?.message.toString(),R.color.colorGreen)
                    //cause delay ....
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(1200)
                        withContext(Dispatchers.Main) {
                            mFragmentListener?.popTopMostFragment()
                        }
                    }
                }
                Status.FAILURE -> showSnackBar(it.errorMsg.toString())
            }
        })
    }
}