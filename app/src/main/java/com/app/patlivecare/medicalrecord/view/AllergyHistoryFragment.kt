package com.app.patlivecare.medicalrecord.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.medicalrecord.viewmodel.AllergyHistoryViewModel
import androidx.lifecycle.Observer
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.medicalrecord.model.AllergyHistoryInfo
import com.app.patlivecare.network.WebHeader
import kotlinx.android.synthetic.main.fragment_allergy_history.*
import kotlinx.android.synthetic.main.fragment_allergy_history.cl_root
import kotlinx.android.synthetic.main.fragment_allergy_history.fbtn_add
import kotlinx.android.synthetic.main.fragment_allergy_history.group_empty_view
import kotlinx.android.synthetic.main.fragment_allergy_history.ibtn_close
import kotlinx.android.synthetic.main.fragment_allergy_history.progress_bar
import kotlinx.android.synthetic.main.fragment_allergy_history.toolbar_main
import kotlinx.android.synthetic.main.fragment_allergy_history.tv_header_title
import java.util.HashMap


class AllergyHistoryFragment : BaseFragment() {
    private var allergyHistoryAdapter:AllergyHistoryAdapter?=null
    private var mFragmentListener: HomeFragmentSelectedListener?=null
    private lateinit var viewModel: AllergyHistoryViewModel
    private var sharedPrefs: SharedPrefHelper? = null
    companion object {
        fun newInstance() = AllergyHistoryFragment()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is HomeFragmentSelectedListener) mFragmentListener = context
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        allergyHistoryAdapter= AllergyHistoryAdapter()
        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        val headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_CONTENT_TYPE] = WebHeader.VAL_CONTENT_TYPE
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer "+accessToken
        viewModel = ViewModelProvider(this).get(AllergyHistoryViewModel::class.java)
        viewModel.headerMap=headerMap
        viewModel.fetchAllergyHistory()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_allergy_history, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_header_title?.text = getString(R.string.title_allergy)
        if(activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(toolbar_main)


        initListener()
        initObserver()


        rv_allergy_history?.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = allergyHistoryAdapter
        }
        allergyHistoryAdapter?.setOnItemClickListener(object :
            AllergyHistoryAdapter.OnItemClickListener {
            override fun onItemMoreClick(model: AllergyHistoryInfo, adapterPosition: Int) {
                viewModel.itemToBeDeleted=adapterPosition
                viewModel.deleteAllergyHistory(model.id)
            }
        })
    }

    private fun initListener() {
        ibtn_close?.setOnClickListener {
            activity?.finish()
        }

        fbtn_add?.setOnClickListener {
            mFragmentListener?.showFragment(FragmentType.ADD_ALLERGY_HISTORY_FRAGMENT,"")
        }

    }

    fun refreshUi() {
           viewModel.fetchAllergyHistory()

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
              //  rv_allergy_history.visibility=View.VISIBLE
                allergyHistoryAdapter?.submitList(it)
            }else{
                group_empty_view.visibility=View.VISIBLE
                allergyHistoryAdapter?.submitList(null)
             //   rv_allergy_history.visibility=View.GONE
            }
        })

        viewModel.resultDeleteRecord.observe(viewLifecycleOwner, Observer {

            when (it.status) {
                Status.SUCCESS ->{
//                  val tempList = viewModel.lstOfMedicalRecord.value?.toMutableList()
                    val tempList=allergyHistoryAdapter?.currentList?.toMutableList()
                    if(!tempList.isNullOrEmpty() &&   tempList.size > viewModel.itemToBeDeleted) {
                        tempList.removeAt(viewModel.itemToBeDeleted)
                        viewModel.lstOfMedicalRecord.value=tempList
                        viewModel.itemToBeDeleted=100000
                        showSnackBar(it.data!!.message,R.color.colorGreen)
                    }

                }
                Status.FAILURE -> showSnackBar(it.errorMsg.toString())
            }
        })

    }



    override fun getRootView(): View {
        return cl_root
    }

}