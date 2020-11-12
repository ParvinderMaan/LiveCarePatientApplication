package com.app.patlivecare.signup.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import com.app.patlivecare.R
import com.app.patlivecare.base.BaseTextWatcher
import kotlinx.android.synthetic.main.fragment_sign_up_one.*
import kotlinx.android.synthetic.main.fragment_sign_up_one.btn_back
import kotlinx.android.synthetic.main.fragment_sign_up_one.btn_continue


class SignUpOneFragment : Fragment() {

    companion object {
        fun newInstance() = SignUpOneFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        btn_back?.setOnClickListener {
            (parentFragment as SignUpFragment).hideSignUpFragment()
        }
        btn_continue?.setOnClickListener {

            process()

        }

        ccp_country?.registerCarrierNumberEditText(edt_phone_no)

        edt_phone_no?.addTextChangedListener(object: BaseTextWatcher(){
            override fun onTextChanged(start: Int, before: Int, count: Int, s: CharSequence?) {
                if (ccp_country.isValidFullNumber) {
                    edt_phone_no?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_green, 0);
                } else {
                    edt_phone_no?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0);
                }
            }
        })

        edt_phone_no?.setOnEditorActionListener { v, actionId, event ->
            if (actionId== EditorInfo.IME_ACTION_GO) {
                process()
            }
            true
        }
    }

    private fun process() {
        val countryCode =  ccp_country.selectedCountryCode
        val phoneNo=edt_phone_no.text.toString()

        if(ccp_country.isValidFullNumber){
             hideKeyboard(ccp_country)
            if (countryCode.isNotEmpty() && phoneNo.isNotEmpty()) {
                (parentFragment as SignUpFragment).signUpRequest?.dialCode=countryCode
                (parentFragment as SignUpFragment).signUpRequest?.phoneNumber=phoneNo.replace(" ","")
                (parentFragment as SignUpFragment).showNextFragment()
            }

        }else{
            edt_phone_no?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
        }

    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}