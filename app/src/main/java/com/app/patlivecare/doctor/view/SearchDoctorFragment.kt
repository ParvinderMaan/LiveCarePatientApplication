package com.app.patlivecare.doctor.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.doctor.model.DoctorInfo
import com.app.patlivecare.doctor.viewmodel.SearchDoctorViewModel
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.network.WebHeader
import com.jakewharton.rxbinding2.widget.RxSearchView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_doctor_speciality.*
import kotlinx.android.synthetic.main.fragment_search_doctor.*
import kotlinx.android.synthetic.main.fragment_search_doctor.cl_root
import kotlinx.android.synthetic.main.fragment_search_doctor.group_empty_view
import kotlinx.android.synthetic.main.fragment_search_doctor.ibtn_close
import kotlinx.android.synthetic.main.fragment_search_doctor.progress_bar
import kotlinx.android.synthetic.main.fragment_search_doctor.search_view
import kotlinx.android.synthetic.main.fragment_search_doctor.toolbar_main
import kotlinx.android.synthetic.main.fragment_search_doctor.tv_header_title
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.util.HashMap
import java.util.concurrent.TimeUnit


class SearchDoctorFragment : BaseFragment() {
    private var mFragmentListener: HomeFragmentSelectedListener? = null
    var searchDoctorAdapter:SearchDoctorAdapter?=null
    private lateinit var viewModel: SearchDoctorViewModel
    private var sharedPrefs: SharedPrefHelper? = null
    private  var disposable: Disposable?=null

    companion object {
        fun newInstance(payload: Any?):SearchDoctorFragment{
            val fragment = SearchDoctorFragment()
            val bundle = Bundle()
           if (payload is String) bundle.putString("KEY",payload)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getRootView(): View {
        return cl_root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeFragmentSelectedListener) mFragmentListener = context
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        val headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_ACCEPT] = WebHeader.VAL_ACCEPT
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer "+accessToken
        searchDoctorAdapter= SearchDoctorAdapter()
        viewModel = ViewModelProvider(this).get(SearchDoctorViewModel::class.java)
        viewModel.headers= headerMap
        arguments?.let {
            viewModel.specialityId = it.getString("KEY","")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_doctor, container, false)
    }

    @SuppressLint("CheckResult")
    @ExperimentalCoroutinesApi
    @FlowPreview
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_header_title?.text = getString(R.string.title_search_your_doctors)       // CATEGORY NAME =
        if(activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(toolbar_main)
        search_view?.isIconified = false // Expand the SearchView with
        search_view?.setOnCloseListener {
          //  activity?.finish()
            return@setOnCloseListener true
        }

        initRecyclerView()
        ibtn_close.setOnClickListener {
            activity?.finish()
        }

        disposable= RxSearchView.queryTextChanges(view.findViewById(R.id.search_view))
            .debounce(500, TimeUnit.MILLISECONDS)
            .filter { text -> text.isNotEmpty() }
            .map { return@map it.toString() }
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { queryText->
                viewModel.fetchSearchDoctor(queryText)
            }
        initObserver()
    }

    private fun initRecyclerView() {
        rv_find_doctor?.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = searchDoctorAdapter
        }
        searchDoctorAdapter?.setOnItemClickListener(object :
            SearchDoctorAdapter.OnItemClickListener {
            override fun onItemClick(model: DoctorInfo, adapterPosition: Int) {
                mFragmentListener?.showFragment(FragmentType.DOCTOR_DETAIL_FRAGMENT,model)
            }
            override fun onItemBookClick(model: DoctorInfo, adapterPosition: Int) {
                mFragmentListener?.showFragment(FragmentType.APPOINTMENT_BOOKING_FRAGMENT,model)
            }
        })
    }

    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner,
            Observer {
                if (it) progress_bar?.show()
                else progress_bar?.hide()
            })

        viewModel.isListEmpty.observe(viewLifecycleOwner,
            Observer {
                if (it){
                    group_empty_view?.visibility = View.VISIBLE
                    search_view?.visibility = View.GONE
                }
            })

        viewModel.lstOfDoctor.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                group_empty_view.visibility=View.GONE
                searchDoctorAdapter?.submitList(it)
            }else{
                group_empty_view.visibility=View.VISIBLE
                searchDoctorAdapter?.submitList(null)
            }
        })

        viewModel.resultSearchDoctor.observe(viewLifecycleOwner,
            Observer {
                when (it.status) {
                    Status.SUCCESS -> viewModel.lstOfDoctor.value=it.data?.data?.dataList
                    Status.FAILURE -> showSnackBar(it.errorMsg.toString())
                }
            })


    }


    override fun onDestroyView() {
        disposable?.dispose()
        super.onDestroyView()
    }

}