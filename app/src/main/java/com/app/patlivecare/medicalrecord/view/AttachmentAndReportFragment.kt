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
import com.app.patlivecare.medicalrecord.viewmodel.AttachmentAndReportViewModel
import com.app.patlivecare.network.WebHeader
import kotlinx.android.synthetic.main.fragment_attachment_and_report.*
import kotlinx.android.synthetic.main.fragment_attachment_and_report.cl_root
import kotlinx.android.synthetic.main.fragment_attachment_and_report.fbtn_add
import kotlinx.android.synthetic.main.fragment_attachment_and_report.group_empty_view
import kotlinx.android.synthetic.main.fragment_attachment_and_report.ibtn_close
import kotlinx.android.synthetic.main.fragment_attachment_and_report.progress_bar
import kotlinx.android.synthetic.main.fragment_attachment_and_report.toolbar_main
import kotlinx.android.synthetic.main.fragment_attachment_and_report.tv_header_title
import java.util.HashMap
import androidx.lifecycle.Observer
import com.app.patlivecare.medicalrecord.model.AttachmentHistoryInfo


class AttachmentAndReportFragment : BaseFragment() {
     private var mAdapter:AttachmentAndReportAdapter?=null
     private var mFragmentListener: HomeFragmentSelectedListener?=null
    private lateinit var viewModel: AttachmentAndReportViewModel
    private var sharedPrefs: SharedPrefHelper? = null
    companion object {
        fun newInstance() = AttachmentAndReportFragment()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()
        if(context is HomeFragmentSelectedListener) mFragmentListener = context
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter= AttachmentAndReportAdapter()
        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        val headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_CONTENT_TYPE] = WebHeader.VAL_CONTENT_TYPE
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer "+accessToken
        viewModel = ViewModelProvider(this).get(AttachmentAndReportViewModel::class.java)
        viewModel.headerMap=headerMap
        viewModel.fetchAttachmentHistory()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_attachment_and_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_header_title?.text = getString(R.string.title_attachment_and_reports)
        if(activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(toolbar_main)

        initListener()
        initObserver()

        rv_attachment_and_reports?.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }
        mAdapter?.setOnItemClickListener(object :
            AttachmentAndReportAdapter.OnItemClickListener {
            override fun onItemMoreClick(model: AttachmentHistoryInfo, adapterPosition: Int) {
                viewModel.itemToBeDeleted=adapterPosition
                viewModel.deleteAttachmentHistory(model.id)
            }

            override fun onItemViewReportClick(model: AttachmentHistoryInfo) {
                mFragmentListener?.showFragment(FragmentType.REPORT_VIEWER_FRAGMENT,model.medicalDocumentPath)
            }


        })

    }

    private fun initListener() {
        ibtn_close?.setOnClickListener {
            activity?.finish()
        }

        fbtn_add?.setOnClickListener {
            mFragmentListener?.showFragment(FragmentType.ADD_ATTACHMENT_AND_REPORT_FRAGMENT,"")
        }
    }

    fun refreshUi() {
        viewModel.fetchAttachmentHistory()

    }
    override fun getRootView(): View {
        return cl_root
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
                rv_attachment_and_reports.visibility=View.VISIBLE
                mAdapter?.submitList(it)
            }else{
                group_empty_view.visibility=View.VISIBLE
                rv_attachment_and_reports.visibility=View.GONE
            }
        })

        viewModel.resultDeleteRecord.observe(viewLifecycleOwner, Observer {

            when (it.status) {
                Status.SUCCESS ->{
                    val tempList = viewModel.lstOfMedicalRecord.value?.toMutableList()
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