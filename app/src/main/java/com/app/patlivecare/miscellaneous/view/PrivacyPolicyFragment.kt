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
import com.app.patlivecare.miscellaneous.viewmodel.PrivacyPolicyViewModel
import kotlinx.android.synthetic.main.fragment_privacy_policy.*
import kotlinx.android.synthetic.main.fragment_privacy_policy.fbtn_read_more
import kotlinx.android.synthetic.main.fragment_privacy_policy.group
import kotlinx.android.synthetic.main.fragment_privacy_policy.ibtn_close
import kotlinx.android.synthetic.main.fragment_privacy_policy.toolbar_main
import kotlinx.android.synthetic.main.fragment_privacy_policy.tv_header_title

class PrivacyPolicyFragment : BaseFragment() {
    private  var mFragmentListener: HomeFragmentSelectedListener?=null
    private lateinit var viewModel: PrivacyPolicyViewModel

    companion object {
        fun newInstance() = PrivacyPolicyFragment()
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
        return inflater.inflate(R.layout.fragment_privacy_policy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(PrivacyPolicyViewModel::class.java)
        super.onViewCreated(view, savedInstanceState)

        tv_header_title?.text = getString(R.string.title_privacy_policy)
        if(activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(toolbar_main)




        fbtn_read_more?.setOnClickListener {
            tv_privacy_policy?.movementMethod = ScrollingMovementMethod()
            tv_privacy_policy.maxLines=Integer.MAX_VALUE
            group?.visibility=View.GONE
        }
        ibtn_close?.setOnClickListener {
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



        viewModel.resultPrivacyPolicy.observe(viewLifecycleOwner, Observer {

            when (it.status) {
                Status.SUCCESS -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        tv_privacy_policy?.text = Html.fromHtml(it.data?.data?.privacyPolicy, Html.FROM_HTML_MODE_LEGACY)
                    } else {
                        @Suppress("DEPRECATION")
                        tv_privacy_policy?.text = Html.fromHtml(it.data?.data?.privacyPolicy)
                    }
                    group.visibility=View.VISIBLE
                }
                Status.FAILURE -> it.errorMsg?.let { showSnackBar(it) }
            }
        })

    }

}