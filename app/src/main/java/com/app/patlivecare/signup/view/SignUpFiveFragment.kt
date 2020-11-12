package com.app.patlivecare.signup.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.patlivecare.R
import com.app.patlivecare.annotation.Gender
import kotlinx.android.synthetic.main.fragment_sign_up_five.*
import kotlinx.android.synthetic.main.fragment_sign_up_five.btn_back
import kotlinx.android.synthetic.main.fragment_sign_up_five.btn_continue

class SignUpFiveFragment : Fragment() {

    private var gender: Int=0

    companion object {
        fun newInstance() = SignUpFiveFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up_five, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ll_male?.setOnClickListener {
            gender=Gender.MALE
            setGenderSelected(isMale = true, isFemale = false, isOther = false)
        }

        ll_female?.setOnClickListener {
            gender=Gender.FEMALE
            setGenderSelected(isMale = false, isFemale = true, isOther = false)
        }

        ll_other?.setOnClickListener {
            gender=Gender.OTHERS
            setGenderSelected(isMale = false, isFemale = false, isOther = true)
        }

        btn_continue?.setOnClickListener {
            if(tv_male.isSelected || tv_female.isSelected ||  tv_other.isSelected){
                (parentFragment as SignUpFragment).signUpRequest?.genderId=gender
                (parentFragment as SignUpFragment).showNextFragment()
            }
        }
        btn_back?.setOnClickListener { (parentFragment as SignUpFragment).showPreviousFragment() }

    }

    private fun setGenderSelected(isMale:Boolean, isFemale:Boolean, isOther:Boolean){
        iv_male?.isSelected=isMale
        iv_female?.isSelected=isFemale
        iv_other?.isSelected=isOther

        ll_male?.isSelected=isMale
        ll_female?.isSelected=isFemale
        ll_other?.isSelected=isOther


        tv_male?.isSelected=isMale
        tv_female?.isSelected=isFemale
        tv_other?.isSelected=isOther

    }

}