package com.app.patlivecare.medicalrecord.view

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.base.isUserInteractionEnabled
import kotlinx.android.synthetic.main.fragment_add_allergy_history.*
import kotlinx.android.synthetic.main.fragment_add_allergy_history.btn_save
import kotlinx.android.synthetic.main.fragment_add_allergy_history.cl_root
import kotlinx.android.synthetic.main.fragment_add_allergy_history.ibtn_close
import kotlinx.android.synthetic.main.fragment_add_allergy_history.toolbar_main
import kotlinx.android.synthetic.main.fragment_add_allergy_history.tv_header_title
import kotlinx.coroutines.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.medicalrecord.model.AllergyHistoryInfo
import com.app.patlivecare.medicalrecord.viewmodel.AddAllergyHistoryViewModel
import com.app.patlivecare.network.WebHeader
import java.util.HashMap


class AddAllergyHistoryFragment : BaseFragment() {
    private lateinit var viewModel: AddAllergyHistoryViewModel
    private var mFragmentListener: HomeFragmentSelectedListener?=null
    private var allergyInfo:AllergyHistoryInfo?=null
    private var sharedPrefs: SharedPrefHelper? = null

    companion object {
        fun newInstance() = AddAllergyHistoryFragment()
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is HomeFragmentSelectedListener) mFragmentListener = context
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        val headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_CONTENT_TYPE] = WebHeader.VAL_CONTENT_TYPE
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer "+accessToken
        viewModel = ViewModelProvider(this).get(AddAllergyHistoryViewModel::class.java)
        viewModel.headerMap=headerMap
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_allergy_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_header_title?.text = getString(R.string.title_add_allergy_history)
        if(activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(toolbar_main)

        initListener()
        initObserver()



    }

    private fun initListener() {
        ibtn_close?.setOnClickListener {
            mFragmentListener?.popTopMostFragment()
        }

        btn_save?.setOnClickListener {
            if(isFormValid())
                allergyInfo?.let { it1 -> viewModel.addAllergyHistory(it1) }
        }
    }

    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it){
                progress_bar?.show()
                btn_save?.visibility=View.INVISIBLE
            }
            else {
                progress_bar?.hide()
                btn_save?.visibility=View.VISIBLE
            }

        })

        viewModel.isViewEnable.observe(viewLifecycleOwner,Observer {
            if (it) view?.isUserInteractionEnabled(true)
            else view?.isUserInteractionEnabled(false)

        })

        viewModel.resultAddRecord.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS ->{
                    showSnackBar(it.data?.message.toString(),R.color.colorGreen)
                    mFragmentListener?.refreshUi(FragmentType.ALLERGY_HISTORY_FRAGMENT)
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
    override fun getRootView(): View {
           return cl_root
    }

    private fun isFormValid(): Boolean {
        val allergyName = edt_allergy_name.text.toString()
        val allergyDesc = edt_allergy_desc.text.toString()

        if (TextUtils.isEmpty(allergyName)
            || TextUtils.isEmpty(allergyDesc)){
            showSnackBar(getString(R.string.alert_please_complete_required_info))
            return false
        }
        allergyInfo= AllergyHistoryInfo()
        allergyInfo?.allergyName=allergyName
        allergyInfo?.description=allergyDesc
        return true
    }

}