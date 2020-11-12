package com.app.patlivecare.miscellaneous.view

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.patlivecare.R
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.miscellaneous.viewmodel.TermAndConditionViewModel
import kotlinx.android.synthetic.main.fragment_terms_and_conditions.*
import kotlinx.android.synthetic.main.fragment_terms_and_conditions.fbtn_read_more
import kotlinx.android.synthetic.main.fragment_terms_and_conditions.group
import kotlinx.android.synthetic.main.fragment_terms_and_conditions.ibtn_close
import kotlinx.android.synthetic.main.fragment_terms_and_conditions.progress_bar
import kotlinx.android.synthetic.main.fragment_terms_and_conditions.toolbar_main
import kotlinx.android.synthetic.main.fragment_terms_and_conditions.tv_header_title

class TermAndConditionFragment : BaseFragment() {
    private  var mFragmentListener: HomeFragmentSelectedListener?=null
    private lateinit var viewModel: TermAndConditionViewModel
    companion object {
        fun newInstance() = TermAndConditionFragment()
    }

    override fun getRootView(): View {
       return cl_root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is HomeFragmentSelectedListener) mFragmentListener = context as HomeFragmentSelectedListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_terms_and_conditions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(TermAndConditionViewModel::class.java)
        super.onViewCreated(view, savedInstanceState)
        fbtn_read_more?.setOnClickListener {
            tv_term_and_condition?.movementMethod = ScrollingMovementMethod()
            tv_term_and_condition.maxLines=Integer.MAX_VALUE
            group?.visibility=View.GONE
        }

        ibtn_close?.setOnClickListener {
            activity?.finish()
        }


        tv_header_title?.text = getString(R.string.title_term_and_condition)
        if(activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(toolbar_main)

        ibtn_close.setOnClickListener {
            activity?.finish()
        }

        viewModel.fetchAppInfo()
        initObserver()
    }

    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner,
            Observer {
                if (it) progress_bar?.show()
                else progress_bar?.hide()
            })

        viewModel.resultTermAndCondition.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        tv_term_and_condition?.text = Html.fromHtml(it.data?.data?.termsConditions, Html.FROM_HTML_MODE_LEGACY)
                    } else {
                        @Suppress("DEPRECATION")
                        tv_term_and_condition?.text = Html.fromHtml(it.data?.data?.termsConditions)
                    }
                    group.visibility=View.VISIBLE
                }
                Status.FAILURE -> it.errorMsg?.let { showSnackBar(it) }
            }
        })

    }
}