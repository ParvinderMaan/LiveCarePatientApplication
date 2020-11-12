package com.app.patlivecare.medicalrecord.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.medicalrecord.model.PastMedicalHistoryInfo
import com.app.patlivecare.medicalrecord.viewmodel.PastMedicalHistoryViewModel
import com.app.patlivecare.network.WebHeader
import kotlinx.android.synthetic.main.fragment_past_medical_history.*
import kotlinx.android.synthetic.main.fragment_past_medical_history.toolbar_main
import kotlinx.android.synthetic.main.fragment_past_medical_history.tv_header_title
import kotlinx.coroutines.*
import java.util.HashMap


class PastMedicalHistoryFragment : BaseFragment() {
    private var mFragmentListener: HomeFragmentSelectedListener?=null
    private var pastMedicalHistoryAdapter:PastMedicalHistoryAdapter?=null
    private lateinit var viewModel: PastMedicalHistoryViewModel
    private var sharedPrefs: SharedPrefHelper? = null

    companion object {
        fun newInstance() = PastMedicalHistoryFragment()
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
        pastMedicalHistoryAdapter=PastMedicalHistoryAdapter()
        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        val headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_CONTENT_TYPE] = WebHeader.VAL_CONTENT_TYPE
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer "+accessToken
        viewModel = ViewModelProvider(this).get(PastMedicalHistoryViewModel::class.java)
        viewModel.headerMap=headerMap
        viewModel.fetchPastMedicalHistory()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_past_medical_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_header_title?.text = getString(R.string.title_past_medical_history)
        if(activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(toolbar_main)

        initListener()




        rv_past_medical_history?.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = pastMedicalHistoryAdapter
        }

        pastMedicalHistoryAdapter?.setOnItemClickListener(object :
            PastMedicalHistoryAdapter.OnItemClickListener{
            override fun onItemMoreClick(model: PastMedicalHistoryInfo, adapterPosition: Int) {
                viewModel.itemToBeDeleted=adapterPosition
                viewModel.deletePastMedicalHistory(model.id)
            }

        })




        initObserver()
    }

    private fun initListener() {
        ibtn_more?.setOnClickListener {
            activity?.finish()
        }

        fbtn_add?.setOnClickListener {
            mFragmentListener?.showFragment(FragmentType.ADD_PAST_MEDICAL_HISTORY_FRAGMENT,"")
        }

    }

    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
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
                       if(group_empty_view.visibility==View.VISIBLE) group_empty_view.visibility=View.GONE
//                     rv_past_medical_history.visibility=View.VISIBLE
                       pastMedicalHistoryAdapter?.submitList(it)
                   }else{
                       group_empty_view.visibility=View.VISIBLE
                       pastMedicalHistoryAdapter?.submitList(null)
//                       rv_past_medical_history.visibility=View.GONE
                   }
        })

        viewModel.resultDeleteRecord.observe(viewLifecycleOwner, Observer {


            when (it.status) {
                Status.SUCCESS ->{
                    val tempList=pastMedicalHistoryAdapter?.currentList?.toMutableList()
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

    fun refreshUi() {
        viewModel.fetchPastMedicalHistory()
    }



}