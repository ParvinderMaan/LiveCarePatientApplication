package com.app.patlivecare.password.view

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.R
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.base.BaseTextWatcher
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.helper.Validator
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.base.isUserInteractionEnabled
import com.app.patlivecare.network.WebHeader
import com.app.patlivecare.password.viewmodel.ChangePasswordViewModel
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.fragment_change_password.progress_bar
import kotlinx.coroutines.*
import java.util.HashMap


class ChangePasswordFragment : BaseFragment() {
    private var oldPassword: String = ""
    private var newPassword: String = ""
    private lateinit var viewModel: ChangePasswordViewModel
    private var sharedPrefs: SharedPrefHelper? = null
    private var accessToken: String = ""
    private var headerMap: HashMap<String, String>? = null
    private var mFragmentListener: HomeFragmentSelectedListener? = null

    companion object {
        fun newInstance() = ChangePasswordFragment()
    }

    override fun getRootView(): View {
       return cl_root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()
        if (context is HomeFragmentSelectedListener) mFragmentListener =
            context as HomeFragmentSelectedListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        headerMap = HashMap()
        headerMap!![WebHeader.KEY_CONTENT_TYPE] = WebHeader.VAL_CONTENT_TYPE
        headerMap!![WebHeader.KEY_AUTHORIZATION] = "Bearer " + accessToken

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(ChangePasswordViewModel::class.java)
        super.onViewCreated(view, savedInstanceState)
        tv_header_title?.text = getString(R.string.title_change_password)
        if(activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(toolbar_main)

        initListener()
        initEditorListener()
        initObserver()

    }

    private fun initEditorListener() {
        edt_current_password?.addTextChangedListener(object: BaseTextWatcher(){
            override fun onTextChanged(start: Int, before: Int, count: Int, s: CharSequence?) {
                if(s.toString().isEmpty()) return

                if (!s.toString().contains(" ")) {
                    edt_current_password?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_green, 0);
                } else {
                    edt_current_password?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0);
                }
            }
        })

        edt_new_password?.addTextChangedListener(object: BaseTextWatcher(){
            override fun onTextChanged(start: Int, before: Int, count: Int, s: CharSequence?) {
                if(s.toString().isEmpty()) return

                if (!s.toString().contains(" ")) {
                    edt_new_password?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_green, 0);
                } else {
                    edt_new_password?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0);
                }
            }
        })

        edt_confirm_new_password?.addTextChangedListener(object: BaseTextWatcher(){
            override fun onTextChanged(start: Int, before: Int, count: Int, s: CharSequence?) {
                if(s.toString().isEmpty()) return

                if (!s.toString().contains(" ")) {
                    edt_confirm_new_password?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_green, 0);
                } else {
                    edt_confirm_new_password?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0);
                }
            }
        })


        edt_confirm_new_password?.setOnEditorActionListener { v, actionId, event ->
            if (actionId== EditorInfo.IME_ACTION_GO) {
                if (isFormValid()) {
                    headerMap?.let { it1 -> viewModel.attemptChangePassword(it1,oldPassword,newPassword ) }
                }
            }
            true
        }
    }

    private fun initListener() {
        ibtn_close?.setOnClickListener {
            activity?.finish()
        }

        btn_reset_password?.setOnClickListener {
            if (isFormValid()) {
                headerMap?.let { it1 -> viewModel.attemptChangePassword(it1,oldPassword,newPassword ) }
            }
        }

    }

    private fun isFormValid(): Boolean {
        var isOldPassword =true
        var isNewPassword =true
        var isConfirmNewPassword =true
        var isPasswordMatch =true
         oldPassword = edt_current_password?.text.toString()
         newPassword = edt_new_password.text.toString()
        var confirmNewPassword = edt_confirm_new_password.text.toString()

        if (TextUtils.isEmpty(oldPassword)) {
            edt_current_password?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
            isOldPassword=false
        }
        if (oldPassword.contains(" ")) {
            edt_current_password?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
            isOldPassword=false
        }
        if (!Validator.isAlphaNumeric(oldPassword)) {
            edt_current_password?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
            isOldPassword=false
        }

        if (TextUtils.isEmpty(newPassword)) {
            edt_new_password?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
            isNewPassword=false
        }
        if (newPassword.contains(" ")) {
            edt_new_password?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
            isNewPassword=false
        }
        if (!Validator.isAlphaNumeric(newPassword)) {
            edt_new_password?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
            isNewPassword=false
        }

        if (TextUtils.isEmpty(confirmNewPassword)) {
            edt_confirm_new_password?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
            isConfirmNewPassword=false
        }
        if (confirmNewPassword.contains(" ")) {
            edt_confirm_new_password?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
            isConfirmNewPassword=false
        }
        if (!Validator.isAlphaNumeric(confirmNewPassword)) {
            edt_confirm_new_password?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
            isConfirmNewPassword=false
        }

        if (!newPassword.equals(confirmNewPassword)) {
            edt_confirm_new_password?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
            isPasswordMatch=false
        }
        edt_current_password.clearFocus()
        edt_new_password.clearFocus()
        edt_confirm_new_password.clearFocus()

        return isOldPassword && isNewPassword && isConfirmNewPassword && isPasswordMatch
    }
    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner,
            Observer {
                if (it) {
                    progress_bar?.visibility = View.VISIBLE
                    btn_reset_password?.visibility = View.INVISIBLE
                    view?.isUserInteractionEnabled(false)

                } else {
                    progress_bar?.visibility = View.INVISIBLE
                    btn_reset_password?.visibility = View.VISIBLE
                    view?.isUserInteractionEnabled(true)

                }
            })



        viewModel.resultantChangePassword.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    showSnackBar(it.data?.message.toString(), R.color.colorGreen)
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(1500)
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