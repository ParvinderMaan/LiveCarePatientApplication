package com.app.patlivecare.profile.view

import android.app.Activity
import android.app.DatePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat.getColor
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.MinorActivity
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.annotation.Verification
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.network.WebHeader
import com.app.patlivecare.profile.model.BasicProfileRequest
import com.app.patlivecare.profile.model.CountryInfoResponse
import com.app.patlivecare.profile.model.StateInfoResponse
import com.app.patlivecare.profile.viewmodel.ProfileBasicViewModel
import kotlinx.android.synthetic.main.fragment_profile_basic.*
import java.util.*


class ProfileBasicFragment : BaseFragment() {
    private lateinit var viewModel: ProfileBasicViewModel
    private var sharedPrefs: SharedPrefHelper? = null
    private lateinit var accessToken: String

    companion object {
        fun newInstance() = ProfileBasicFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accessToken= sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN,"").toString()
        val headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_CONTENT_TYPE] = WebHeader.VAL_CONTENT_TYPE
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer "+accessToken
        viewModel = ViewModelProvider(this).get(ProfileBasicViewModel::class.java)
        viewModel.headerMap=headerMap

        LocalBroadcastManager.getInstance(activity as Context)
            .registerReceiver(mMessageReceiver, IntentFilter("event_refresh_ui"));
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_basic, container, false)
    }

    override fun getRootView(): View {
        return fl_root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchBasicInfo()

        initObserver()
        initListener()
        initEditorListener()
        setViewEnable(false)
    }

    private fun initEditorListener() {
        edt_first_name?.setOnEditorActionListener { v, actionId, event ->
            if (actionId== EditorInfo.IME_ACTION_NEXT) {
                edt_last_name.text?.length?.let { edt_last_name.setSelection(it) }
                edt_last_name?.requestFocus()

            }
            true
        }

        edt_last_name?.setOnEditorActionListener { v, actionId, event ->
            if (actionId== EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(edt_last_name)
                edt_last_name.clearFocus()
            }
            true
        }

        edt_city?.setOnEditorActionListener { v, actionId, event ->
            if (actionId== EditorInfo.IME_ACTION_NEXT) {
                edt_current_addr.text?.length?.let { edt_current_addr.setSelection(it) }
                edt_current_addr?.requestFocus()
            }
            true
        }

        edt_current_addr?.setOnEditorActionListener { v, actionId, event ->
            if (actionId== EditorInfo.IME_ACTION_NEXT) {
                edt_email.text?.length?.let { edt_email.setSelection(it) }
                edt_email?.requestFocus()
            }
            true
        }

        edt_email?.setOnEditorActionListener { v, actionId, event ->
            if (actionId== EditorInfo.IME_ACTION_NEXT) {
                edt_phone_no.text?.length?.let { edt_phone_no.setSelection(it) }
                edt_phone_no?.requestFocus()
            }
            true
        }

        edt_phone_no?.setOnEditorActionListener { v, actionId, event ->
            if (actionId== EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(edt_phone_no)
            }
            true
        }

    }

    private fun initListener() {
        btn_edit.tag=0
        btn_edit?.setOnClickListener {
            when (btn_edit.tag) {
                0 -> {
                    btn_edit.tag = 1
                    btn_edit.text = getString(R.string.action_save)
                    setViewEnable(true)
                }
                1 -> {
                    if(isFormValid())
                        viewModel.updateBasicProfileInfo()
//                    btn_edit.tag=0
//                    btn_edit.text=getString(R.string.action_edit)
//                    setViewEnable(false)
                }
            }

        }
        edt_dob?.setOnClickListener { showDatePicker() }


        edt_gender?.setOnClickListener{
            showGenderPopupMenu()
        }

        edt_country?.setOnClickListener{
            viewModel.lstOfCountries.value?.let {
                val addressDialogFragment: AddressDialogFragment = AddressDialogFragment.newInstance("C",it)
                addressDialogFragment.setOnCountryDialogListener(object:AddressDialogFragment.CountryDialogListener{
                    override fun onClickCountry(countryInfo: CountryInfoResponse.Country) {
                        edt_country.tag = countryInfo.id
                        edt_country.setText(countryInfo.name)
                        viewModel.fetchState(countryInfo.id)
                        addressDialogFragment.dismiss()
                    }
                })
                addressDialogFragment.show(childFragmentManager, "TAG")
          }

        }

        edt_state?.setOnClickListener{
            viewModel.lstOfStates.value?.let {
                val addressDialogFragment: AddressDialogFragment = AddressDialogFragment.newInstance("S",it)
                addressDialogFragment.setOnStateDialogListener(object:AddressDialogFragment.StateDialogListener{
                    override fun onClickState(stateInfo: StateInfoResponse.State) {
                        edt_state.tag = stateInfo.id
                        edt_state.setText(stateInfo.name)
                        addressDialogFragment.dismiss()
                    }

                })
                addressDialogFragment.show(childFragmentManager, "TAG")
            }
        }

        tv_verify_email?.setOnClickListener{
            if(edt_email.text.toString().isNotEmpty()){
                val intent= Intent(activity, MinorActivity::class.java)
                intent.putExtra("fragment_type", FragmentType.VERIFICATION_FRAGMENT)
                intent.putExtra("verify",Verification.EMAIL)
                startActivity(intent)
            }
        }

        tv_verify_phone_no?.setOnClickListener{
            if(edt_phone_no.text.toString().isNotEmpty()){
                val phoneNumber =  edt_phone_no.text.toString().replace(" ","")
                val dialCode =  ccp_country.selectedCountryCode
                sharedPrefs?.builder()?.write(SharedPrefHelper.KEY_PHONE, dialCode+"|"+phoneNumber)?.build()
                val intent= Intent(activity, MinorActivity::class.java)
                intent.putExtra("fragment_type", FragmentType.VERIFICATION_FRAGMENT)
                intent.putExtra("verify",Verification.PHONE)
                startActivity(intent)
            }
        }
    }

    private fun setViewEnable(isViewEnable: Boolean) {
        edt_first_name?.isEnabled=isViewEnable
        edt_last_name?.isEnabled=isViewEnable
        edt_gender?.isEnabled=isViewEnable
//      edt_email?.isEnabled=isViewEnable   // no need !!
        edt_dob?.isEnabled=isViewEnable
        edt_city?.isEnabled=isViewEnable
        edt_state?.isEnabled=isViewEnable
        edt_country?.isEnabled=isViewEnable
        edt_current_addr?.isEnabled=isViewEnable
        ccp_country?.isClickable=isViewEnable
        edt_phone_no?.isEnabled=isViewEnable

        tv_verify_email.isEnabled=!isViewEnable
        tv_verify_phone_no.isEnabled=!isViewEnable
    }



    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner,
            Observer {
                if (it) {
                    progress_bar?.visibility = View.VISIBLE
                    sv_main?.visibility = View.INVISIBLE
                }
                else {
                    progress_bar?.visibility = View.INVISIBLE
                    sv_main?.visibility = View.VISIBLE
                }
            })

//        viewModel.isViewEnable.observe(viewLifecycleOwner,
//            Observer {
////                fbtn_sign_in?.isClickable = it
//            }
//        )

        viewModel.resultBasicInfo.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS ->{
                    viewModel.basicInfoData.value=it.data?.data
                    viewModel.fetchCountries()
                }
                Status.FAILURE -> showSnackBar(it.errorMsg.toString())
            }
        })

        viewModel.resultUpdateBasicInfo.observe(viewLifecycleOwner, Observer {
        btn_edit.tag=0
        btn_edit.text=getString(R.string.action_edit)
        setViewEnable(false)
            when (it.status) {
                Status.SUCCESS ->{
                    showSnackBar(it.data!!.message,R.color.colorGreen)
                    val address =viewModel.basicProRequest.city.plus("|")
                           .plus(viewModel.basicProRequest.state).plus("|")
                           .plus(viewModel.basicProRequest.country)
                    address.let {
                        sharedPrefs?.builder()?.write(SharedPrefHelper.KEY_ADDRESS,address)?.build()
                        val intent = Intent("event_refresh_navi_header")
                        LocalBroadcastManager.getInstance(activity as Context).sendBroadcast(intent)
                    }

                }
                Status.FAILURE -> showSnackBar(it.errorMsg.toString())
            }
        })

        viewModel.resultCountries.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> viewModel.lstOfCountries.value=it.data?.data?.listOfCountries
                Status.FAILURE -> showSnackBar(it.errorMsg.toString())
            }
        })


        viewModel.resultStates.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> viewModel.lstOfStates.value=it.data?.data?.listOfStates
                Status.FAILURE -> showSnackBar(it.errorMsg.toString())
            }
        })


        viewModel.basicInfoData.observe(viewLifecycleOwner, Observer {
            edt_first_name?.setText(it?.firstName)
            edt_last_name?.setText(it?.lastName)
            edt_gender?.setText(it?.gender)
            edt_email?.setText(it?.email)
            edt_city?.setText(it?.city)
            edt_state?.setText(it?.state)
            edt_country?.setText(it?.country)
            edt_current_addr?.setText(it?.currentAddress)
            edt_dob?.setText(it?.dateOfBirth)

            it?.dialCode?.let {itt->
                if(itt.isNotEmpty())ccp_country?.setCountryForPhoneCode(itt.toInt())
                else  ccp_country?.setAutoDetectedCountry(true)
            }
            it?.phoneNumber?.let{ itt->
               if(itt.isNotEmpty())edt_phone_no?.setText(itt)
               else  ccp_country?.setAutoDetectedCountry(true)
            }

            if(it.isEmailVerified){
                tv_verify_email?.text =getString(R.string.action_verified)
                tv_verify_email?.setTextColor(getColor(activity as Context,R.color.colorGreen))
                tv_verify_email?.setOnClickListener(null)
            }else{
                tv_verify_email?.text =getString(R.string.action_verify)
                tv_verify_email?.setTextColor(getColor(activity as Context,R.color.colorDarkRed))
            }

            if(it.isPhoneNoVerified){
                tv_verify_phone_no?.text =getString(R.string.action_verified)
                tv_verify_phone_no?.setTextColor(getColor(activity as Context,R.color.colorGreen))
                tv_verify_phone_no?.setOnClickListener(null)
            }else{
                tv_verify_phone_no?.text =getString(R.string.action_verify)
                tv_verify_phone_no?.setTextColor(getColor(activity as Context,R.color.colorDarkRed))
            }




           edt_gender?.tag =it.genderId
           edt_country?.tag=it.countryId
           edt_state?.tag=it.stateId

        })


    }


    private fun isFormValid(): Boolean {
        val firstName = edt_first_name.text.toString()
        val lastName = edt_last_name.text.toString()
        val dateOfBirth = edt_dob.text.toString()
        val gender = edt_gender.text.toString()
        val country = edt_country.text.toString()
        val state = edt_state.text.toString()
        val city = edt_city.text.toString()
        val currentAddr = edt_current_addr.text.toString()
        val phoneNumber =  edt_phone_no.text.toString().replace(" ","")

        if (TextUtils.isEmpty(firstName)
            || TextUtils.isEmpty(lastName)
            || TextUtils.isEmpty(dateOfBirth)
            || TextUtils.isEmpty(gender)
            || TextUtils.isEmpty(country)
            || TextUtils.isEmpty(state)
            || TextUtils.isEmpty(city)
            || TextUtils.isEmpty(currentAddr)
            || TextUtils.isEmpty(phoneNumber)) {
            showSnackBar(getString(R.string.alert_please_complete_required_info))
            return false
        }

        val countryId = edt_country.tag as Int
        val stateId =edt_state.tag as Int
        val genderId = edt_gender.tag as Int
        val dialCode =  ccp_country.selectedCountryCode

        viewModel.basicProRequest=BasicProfileRequest(city,
         countryId,
         currentAddr,
         dateOfBirth,
         dialCode,
         firstName,
         genderId,
         lastName,
         phoneNumber,
         stateId,
         state,
        country)
        return true
    }

    private fun showGenderPopupMenu() {
        val popupMenu = PopupMenu(requireActivity(), edt_gender)
        val menu = popupMenu.menu
        menu.add(0, 1, 0, getString(R.string.title_male))
        menu.add(0, 2, 0, getString(R.string.title_female))
        menu.add(0, 3, 0, getString(R.string.title_other))
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            edt_gender?.setText(item.title)
            edt_gender?.tag = item.itemId
            false
        }
        popupMenu.show()

    }
    /*     dd-MM-yyyy     */
    private fun showDatePicker() {
        val initYear: Int
        val initMonth: Int
        val initDay: Int
        val c = Calendar.getInstance()
        val selectedDob: String = edt_dob?.text.toString()
        if (TextUtils.isEmpty(selectedDob)) {
            initYear = c[Calendar.YEAR]
            initMonth = c[Calendar.MONTH]
            initDay = c[Calendar.DAY_OF_MONTH]
        } else {
            val userSelected = selectedDob.split("-".toRegex()).toTypedArray()
            initYear = userSelected[2].toInt()
            initDay = userSelected[0].toInt()
            initMonth = userSelected[1].toInt() - 1
        }
        val datePickerDialog: DatePickerDialog
        datePickerDialog = DatePickerDialog(requireActivity(), R.style.PickerStyle,
            DatePickerDialog.OnDateSetListener { view1: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val dayOfMonthh = String.format(Locale.getDefault(), "%02d", dayOfMonth)
                val monthOfYearr = String.format(Locale.getDefault(), "%02d", monthOfYear + 1)
                val x = dayOfMonthh + "-" + monthOfYearr + "-" + year
                edt_dob?.setText(x)

            },
            initYear,
            initMonth,
            initDay
        )
        datePickerDialog.datePicker.maxDate = c.timeInMillis - 568025136000L // -18yrs
        datePickerDialog.setOnShowListener {
            val greenColor = getColor(activity as Context, R.color.colorGreen)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(greenColor)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(greenColor)
        }
        datePickerDialog.show()
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            viewModel.fetchBasicInfo()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(activity as Context).unregisterReceiver(
            mMessageReceiver)

    }
}