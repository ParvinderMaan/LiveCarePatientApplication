package com.app.patlivecare.signup.view

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.app.patlivecare.R
import com.app.patlivecare.base.BaseTextWatcher
import kotlinx.android.synthetic.main.fragment_sign_up_four.*


class SignUpFourFragment : Fragment() {



    companion object {
        fun newInstance() = SignUpFourFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up_four, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_continue?.setOnClickListener {
            if (isFormValid()) {
                hideKeyboard(btn_continue)
                val firstName = edt_first_name?.text.toString()
                val lastName = edt_last_name?.text.toString()
                (parentFragment as SignUpFragment).signUpRequest?.firstName=firstName
                (parentFragment as SignUpFragment).signUpRequest?.lastName=lastName
                (parentFragment as SignUpFragment).showNextFragment()
            }
        }


        btn_back?.setOnClickListener { (parentFragment as SignUpFragment).showPreviousFragment() }

        edt_first_name?.addTextChangedListener(object: BaseTextWatcher(){
            override fun onTextChanged(start: Int, before: Int, count: Int, s: CharSequence?) {
                if (TextUtils.isEmpty(s.toString())) {
                    edt_first_name?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
                }else{
                    edt_first_name?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_green, 0)
                }

            }
        })

        edt_last_name?.addTextChangedListener(object: BaseTextWatcher(){
            override fun onTextChanged(start: Int, before: Int, count: Int, s: CharSequence?) {
                if (TextUtils.isEmpty(s.toString())) {
                    edt_last_name?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
                }else{
                    edt_last_name?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_green, 0)
                }

            }
        })

        edt_first_name?.setOnEditorActionListener { v, actionId, event ->
            if (actionId==EditorInfo.IME_ACTION_NEXT) {
                edt_last_name?.requestFocus()
            }
            true
        }

        edt_last_name?.setOnEditorActionListener { v, actionId, event ->
            if (actionId==EditorInfo.IME_ACTION_GO) {
                if (isFormValid()) {
                    hideKeyboard(btn_continue)
                    val firstName = edt_first_name?.text.toString()
                    val lastName = edt_last_name?.text.toString()
                    (parentFragment as SignUpFragment).signUpRequest?.firstName=firstName
                    (parentFragment as SignUpFragment).signUpRequest?.lastName=lastName
                    (parentFragment as SignUpFragment).showNextFragment()
                }

            }
            true
        }


    }

    private fun isFormValid(): Boolean {
        var isFirstName:Boolean=true
        var isLastName:Boolean=true
        var firstName = edt_first_name.text.toString()
        var lastName = edt_last_name.text.toString()
        if (TextUtils.isEmpty(firstName)) {
            edt_first_name?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
            isFirstName= false
        }
        if (TextUtils.isEmpty(lastName)) {
            edt_last_name?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
            isLastName= false
        }

        return isFirstName && isLastName
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}