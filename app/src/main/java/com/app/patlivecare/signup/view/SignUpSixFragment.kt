package com.app.patlivecare.signup.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.base.BaseTextWatcher
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.helper.Validator
import com.app.patlivecare.home.view.HomeActivity
import com.app.patlivecare.base.isUserInteractionEnabled
import com.app.patlivecare.signup.model.SignUpResponse
import com.app.patlivecare.signup.viewmodel.SignUpSixViewModel
import kotlinx.android.synthetic.main.fragment_sign_up_six.*
import kotlinx.android.synthetic.main.fragment_sign_up_six.cl_root
import kotlinx.android.synthetic.main.fragment_sign_up_six.progress_bar


class SignUpSixFragment : BaseFragment() {
    private lateinit var viewModel: SignUpSixViewModel
    private var isUserAgree: Boolean = false
    private var sharedPrefs: SharedPrefHelper? = null

    companion object {
        fun newInstance() = SignUpSixFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up_six, container, false)
    }

    override fun getRootView(): View {
        return cl_root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(SignUpSixViewModel::class.java)
        super.onViewCreated(view, savedInstanceState)
        initListener()
        initObserver()

        // hide password
        ibtn_password_eye.isSelected = true
        edt_password?.transformationMethod = PasswordTransformationMethod.getInstance()
        edt_password?.addTextChangedListener(object : BaseTextWatcher() {
            override fun onTextChanged(start: Int, before: Int, count: Int, s: CharSequence?) {
                if (s!!.length < 6) {
                    tv_label_i?.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_tick_red,
                        0,
                        0,
                        0
                    );
                } else {
                    tv_label_i?.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_tick_green,
                        0,
                        0,
                        0
                    );
                }
                if (Validator.isUpperAndLowerCase(s.toString())) {
                    tv_label_ii?.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_tick_green,
                        0,
                        0,
                        0
                    );
                } else {
                    tv_label_ii?.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_tick_red,
                        0,
                        0,
                        0
                    );
                }
                if (Validator.isDigitAndSymbolCase(s.toString())) {
                    tv_label_iii?.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_tick_green,
                        0,
                        0,
                        0
                    );
                } else {
                    tv_label_iii?.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_tick_red,
                        0,
                        0,
                        0
                    );
                }
            }
        })
        edt_password?.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                val password = edt_password?.text.toString()
                if (password.length > 6 && Validator.isUpperAndLowerCase(password) && Validator.isDigitAndSymbolCase(
                        password
                    )
                ) {
                    if (isUserAgree) {
                        (parentFragment as SignUpFragment).signUpRequest?.password = password
//                      (parentFragment as SignUpFragment).attemptSignIn()
                        viewModel.signUpRequest = (parentFragment as SignUpFragment).signUpRequest
                        viewModel.attemptSignUp()
                    } else {
                        hideKeyboard(edt_password)
                        showSnackBar(getString(R.string.alert_accept_term_of_use))
                    }

                }
            }
            true
        }

    }


    private fun initListener() {
        tv_terms_of_use?.setOnClickListener {
            (parentFragment as SignUpFragment).showFragment(FragmentType.TERM_AND_CONDITION_FRAGMENT)
        }
        tv_privacy_policy?.setOnClickListener {
            (parentFragment as SignUpFragment).showFragment(FragmentType.PRIVACY_POLICY_FRAGMENT)
        }

        ibtn_password_eye?.setOnClickListener {
            if (ibtn_password_eye.isSelected) {
                ibtn_password_eye.isSelected = false
                // show password
                edt_password?.transformationMethod = HideReturnsTransformationMethod.getInstance()
                if (edt_password.text != null && edt_password.text?.length != 0)
                    edt_password.text?.length?.let { it1 -> edt_password.setSelection(it1) }
            } else {
                ibtn_password_eye.isSelected = true
                // hide password
                edt_password?.transformationMethod = PasswordTransformationMethod.getInstance()
                if (edt_password.text != null && edt_password.text?.length != 0)
                    edt_password.text?.length?.let { it1 -> edt_password.setSelection(it1) }
            }

        }


        iv_agree?.setOnClickListener {
            if (iv_agree.isSelected) {
                isUserAgree = false
                iv_agree.isSelected = false
            } else {
                isUserAgree = true
                iv_agree.isSelected = true
            }
        }

        btn_back?.setOnClickListener { (parentFragment as SignUpFragment).showPreviousFragment() }

        btn_create_account?.setOnClickListener {
            val password = edt_password.text.toString()
            if (password.length > 6 && Validator.isUpperAndLowerCase(password) && Validator.isDigitAndSymbolCase(
                    password
                )
            ) {
                if (isUserAgree) {
                    (parentFragment as SignUpFragment).signUpRequest?.password = password
                    viewModel.signUpRequest = (parentFragment as SignUpFragment).signUpRequest
                    viewModel.attemptSignUp()
                } else {
                    showSnackBar(getString(R.string.alert_accept_term_of_use))
                }


            }
        }

    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner,
            Observer {
                if (it) progress_bar?.show()
                else progress_bar?.hide()
            })

        viewModel.isViewEnable.observe(viewLifecycleOwner,
            Observer {
                if (it) {
                    btn_back?.visibility = View.VISIBLE
                    btn_create_account?.visibility = View.VISIBLE
                    view?.isUserInteractionEnabled(true)
                } else {
                    btn_back?.visibility = View.INVISIBLE
                    btn_create_account?.visibility = View.INVISIBLE
                    view?.isUserInteractionEnabled(false)
                }
            })

        viewModel.resultantSignUp.observe(viewLifecycleOwner, Observer {

            when (it.status) {
                Status.SUCCESS -> processUserInfo(it.data?.data)
                Status.FAILURE -> it.errorMsg?.let { showSnackBar(it) }
            }
        })
    }

    private fun processUserInfo(data: SignUpResponse.Data?) {
//        data?.firstName?.let { sharedPrefs?.write(SharedPrefHelper.KEY_FIRST_NAME, it) }
//        data?.lastName?.let { sharedPrefs?.write(SharedPrefHelper.KEY_LAST_NAME, it) }
//        data?.gender?.let { sharedPrefs?.write(SharedPrefHelper.KEY_GENDER, it) }
//        data?.email?.let { sharedPrefs?.write(SharedPrefHelper.KEY_EMAIL, it) }
//        data?.profilePic?.let { sharedPrefs?.write(SharedPrefHelper.KEY_PROFILE_PIC, it) }
//        data?.emailNotificationStatus?.let { sharedPrefs?.write(SharedPrefHelper.KEY_EMAIL_NOTIFICATION, it) }
//        data?.smsNotificationStatus?.let { sharedPrefs?.write(SharedPrefHelper.KEY_SMS_NOTIFICATION, it) }
//        data?.accessToken?.let { sharedPrefs?.write(SharedPrefHelper.KEY_ACCESS_TOKEN, it) }
//        val address = data?.city.plus("|").plus(data?.state).plus("|").plus(data?.country)
//        sharedPrefs?.write(SharedPrefHelper.KEY_ADDRESS, address)
//        sharedPrefs?.write(SharedPrefHelper.KEY_IS_SIGN_IN, true)

        val address = data?.city.plus("|").plus(data?.state).plus("|").plus(data?.country)
        // comment
        sharedPrefs?.builder()
            ?.write(SharedPrefHelper.KEY_FIRST_NAME, data!!.firstName)
            ?.write(SharedPrefHelper.KEY_LAST_NAME, data.lastName)
            ?.write(SharedPrefHelper.KEY_GENDER, data.gender)
            ?.write(SharedPrefHelper.KEY_EMAIL, data.email)
            ?.write(SharedPrefHelper.KEY_PHONE, data.dialCode+"|"+data.phoneNumber)
            ?.write(SharedPrefHelper.KEY_PROFILE_PIC, data.profilePic)
            ?.write(SharedPrefHelper.KEY_EMAIL_NOTIFICATION, data.emailNotificationStatus)
            ?.write(SharedPrefHelper.KEY_SMS_NOTIFICATION, data.smsNotificationStatus)
            ?.write(SharedPrefHelper.KEY_ACCESS_TOKEN, data.accessToken)
            ?.write(SharedPrefHelper.KEY_ADDRESS, address)
            ?.write(SharedPrefHelper.KEY_IS_SIGN_IN, true)
            ?.write(SharedPrefHelper.KEY_PROFILE_PERCENT,data.percentageProfileComplete)
            ?.write(SharedPrefHelper.KEY_IS_SOCIAL_SIGN_IN,data.isSocialUser)
            ?.build()

        val intent = Intent(activity, HomeActivity::class.java)
        startActivity(intent)
        activity?.finish()



    }


}

