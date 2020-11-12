package com.app.patlivecare.medicalrecord.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.medicalrecord.viewmodel.FamilyHistoryViewModel
import com.app.patlivecare.network.WebHeader
import kotlinx.android.synthetic.main.fragment_family_history.*
import kotlinx.android.synthetic.main.fragment_family_history.cl_root
import kotlinx.android.synthetic.main.fragment_family_history.fbtn_add
import kotlinx.android.synthetic.main.fragment_family_history.group_empty_view
import kotlinx.android.synthetic.main.fragment_family_history.progress_bar
import kotlinx.android.synthetic.main.fragment_family_history.toolbar_main
import kotlinx.android.synthetic.main.fragment_family_history.tv_header_title
import java.util.HashMap
import androidx.lifecycle.Observer
import com.app.patlivecare.medicalrecord.model.FamilyHistoryInfo


class FamilyHistoryFragment : BaseFragment() {
    private var familyHistoryAdapter:FamilyHistoryAdapter?=null
    private var mFragmentListener: HomeFragmentSelectedListener?=null
    private lateinit var viewModel: FamilyHistoryViewModel
    private var sharedPrefs: SharedPrefHelper? = null
    companion object {
        fun newInstance() = FamilyHistoryFragment()
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
        familyHistoryAdapter= FamilyHistoryAdapter()
        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        val headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_CONTENT_TYPE] = WebHeader.VAL_CONTENT_TYPE
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer "+accessToken
        viewModel = ViewModelProvider(this).get(FamilyHistoryViewModel::class.java)
        viewModel.headerMap=headerMap
        viewModel.fetchFamilyHistory()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_family_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_header_title?.text = getString(R.string.title_family_history)
        if(activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(toolbar_main)

        initListener()
        initObserver()

        rv_family_history?.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = familyHistoryAdapter

        }
        familyHistoryAdapter?.setOnItemClickListener(object :
            FamilyHistoryAdapter.OnItemClickListener {
            override fun onItemMoreClick(model: FamilyHistoryInfo, adapterPosition: Int) {
                viewModel.itemToBeDeleted=adapterPosition
                viewModel.deleteFamilyHistory(model.id)
            }


        })
    }

    private fun initListener() {
        ibtn_close?.setOnClickListener {
            activity?.finish()
        }

        fbtn_add?.setOnClickListener {
            mFragmentListener?.showFragment(FragmentType.ADD_FAMILY_HISTORY_FRAGMENT,"")
        }

    }

    fun refreshUi() {
        viewModel.fetchFamilyHistory()
    }

    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner,Observer {
            if (it) progress_bar?.show()
            else progress_bar?.hide()
        })

        viewModel.resultFetchRecords.observe(viewLifecycleOwner, Observer {
            fbtn_add?.show()
            when (it.status) {
                Status.SUCCESS -> viewModel.lstOfMedicalRecord.value=it.data?.data
                Status.FAILURE -> showSnackBar(it.errorMsg.toString())
            }
        })

        viewModel.lstOfMedicalRecord.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                group_empty_view.visibility=View.GONE
             //   rv_family_history.visibility=View.VISIBLE
                familyHistoryAdapter?.submitList(it)
            }else{
                group_empty_view.visibility=View.VISIBLE
              //  rv_family_history.visibility=View.GONE
                familyHistoryAdapter?.submitList(null)
            }
        })

        viewModel.resultDeleteRecord.observe(viewLifecycleOwner, Observer {

            when (it.status) {
                Status.SUCCESS ->{
           //       val tempList = viewModel.lstOfMedicalRecord.value?.toMutableList()
                    val tempList=familyHistoryAdapter?.currentList?.toMutableList()
                    if(!tempList.isNullOrEmpty() &&   tempList.size > viewModel.itemToBeDeleted) {
                        tempList.removeAt(viewModel.itemToBeDeleted)
                        viewModel.lstOfMedicalRecord.value=tempList
                        viewModel.itemToBeDeleted=100000
                    }

                }
                Status.FAILURE -> showSnackBar(it.errorMsg.toString())
            }
        })

    }

}

