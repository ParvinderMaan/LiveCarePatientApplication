package com.app.patlivecare.medicalrecord.view

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.base.isUserInteractionEnabled
import com.app.patlivecare.medicalrecord.model.FamilyHistoryInfo
import com.app.patlivecare.medicalrecord.viewmodel.AddFamilyHistoryViewModel
import kotlinx.android.synthetic.main.fragment_add_family_history.*
import kotlinx.android.synthetic.main.fragment_add_family_history.btn_save
import kotlinx.android.synthetic.main.fragment_add_family_history.cl_root
import kotlinx.android.synthetic.main.fragment_add_family_history.ibtn_close
import kotlinx.android.synthetic.main.fragment_add_family_history.progress_bar
import kotlinx.android.synthetic.main.fragment_add_family_history.toolbar_main
import kotlinx.android.synthetic.main.fragment_add_family_history.tv_header_title
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.network.WebHeader

import kotlinx.coroutines.*
import java.util.HashMap


class AddFamilyHistoryFragment : BaseFragment() {
    private var mFragmentListener: HomeFragmentSelectedListener?=null
    private var familyInfo:FamilyHistoryInfo?=null
    private lateinit var viewModel: AddFamilyHistoryViewModel
    private var sharedPrefs: SharedPrefHelper? = null

    companion object {
        fun newInstance() = AddFamilyHistoryFragment()
    }

    override fun getRootView(): View {
        return cl_root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()
        if(context is HomeFragmentSelectedListener) mFragmentListener = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        val headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_CONTENT_TYPE] = WebHeader.VAL_CONTENT_TYPE
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer "+accessToken
        viewModel = ViewModelProvider(this).get(AddFamilyHistoryViewModel::class.java)
        viewModel.headerMap=headerMap
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_family_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_header_title?.text = getString(R.string.title_add_family_history)
        if(activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(toolbar_main)

        ibtn_close?.setOnClickListener {
            mFragmentListener?.popTopMostFragment()
        }

        btn_save?.setOnClickListener {
            if(isFormValid())
                familyInfo?.let { it1 -> viewModel.addFamilyHistory(it1) }
        }

        edt_age?.setOnClickListener {
            showAgePopupMenu()
        }
        initObserver()
    }

    private fun isFormValid(): Boolean {
        val diseaseName = edt_disease_name.text.toString()
        val memberName = edt_member_name.text.toString()
        val age = edt_age.text.toString()
        val relation = edt_relation.text.toString()
        val desc =edt_desc.text.toString()
        if (TextUtils.isEmpty(diseaseName)
            || TextUtils.isEmpty(memberName)
            || TextUtils.isEmpty(age)
            || TextUtils.isEmpty(relation)
            || TextUtils.isEmpty(desc)){
            showSnackBar(getString(R.string.alert_please_complete_required_info))
            return false
        }
        familyInfo= FamilyHistoryInfo()
        familyInfo?.diseaseName=diseaseName
        familyInfo?.memberName=memberName
        familyInfo?.age=age.toInt()
        familyInfo?.relation=relation
        familyInfo?.description=desc
        return true
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
                    mFragmentListener?.refreshUi(FragmentType.FAMILY_HISTORY_FRAGMENT)
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


    private fun showAgePopupMenu() {
        val popupMenu = PopupMenu(requireActivity(), edt_age)
        val menu = popupMenu.menu
        (1..120).forEach {
            menu.add(0, it, 0, it.toString())
        }
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            edt_age?.setText(item.title)
            false
        }
        popupMenu.show()
    }
}