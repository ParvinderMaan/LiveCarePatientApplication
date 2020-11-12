package com.app.patlivecare.signup.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.app.patlivecare.R
import com.app.patlivecare.base.BaseTextWatcher
import com.app.patlivecare.helper.Validator
import kotlinx.android.synthetic.main.fragment_sign_up_three.*
import kotlinx.android.synthetic.main.fragment_sign_up_three.btn_back
import kotlinx.android.synthetic.main.fragment_sign_up_three.btn_continue


class SignUpThreeFragment : Fragment() {

    companion object {
        fun newInstance() = SignUpThreeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up_three, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_continue?.setOnClickListener {
          process()

        }
        btn_back?.setOnClickListener { (parentFragment as SignUpFragment).showPreviousFragment() }

        edt_email?.addTextChangedListener(object: BaseTextWatcher(){
            override fun onTextChanged(start: Int, before: Int, count: Int, s: CharSequence?) {
                if (Validator.isEmailValid(s.toString())) {
                    edt_email?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email, 0, R.drawable.ic_tick_green, 0);
                } else {
                    edt_email?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email, 0, R.drawable.ic_tick_red, 0);
                }
            }
        })


        edt_email?.setOnEditorActionListener { v, actionId, event ->
                if (actionId== EditorInfo.IME_ACTION_GO) {
                    process()
                }
                true
            }
    }

    private fun process() {
        val email=edt_email?.text.toString()

        if(Validator.isEmailValid(email)){
            (parentFragment as SignUpFragment).signUpRequest?.email=email
            (parentFragment as SignUpFragment).showNextFragment()
        }else{
            edt_email?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email, 0, R.drawable.ic_tick_red, 0);
        }

    }



}