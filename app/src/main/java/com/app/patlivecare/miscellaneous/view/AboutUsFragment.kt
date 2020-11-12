package com.app.patlivecare.miscellaneous.view

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.patlivecare.R
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.miscellaneous.viewmodel.AboutUsViewModel
import kotlinx.android.synthetic.main.fragment_about_us.*
import kotlinx.android.synthetic.main.fragment_about_us.progress_bar


class AboutUsFragment : BaseFragment() {
    private lateinit var viewModel: AboutUsViewModel

   // private  var mFragmentListener: HomeFragmentSelectedListener?=null

    companion object {
        fun newInstance() = AboutUsFragment()
    }

    override fun getRootView(): View {
        return cl_root_
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
       // mFragmentListener = context as HomeFragmentSelectedListener
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about_us, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(AboutUsViewModel::class.java)
        super.onViewCreated(view, savedInstanceState)

        fbtn_read_more?.setOnClickListener {
            tv_about_us?.movementMethod = ScrollingMovementMethod()
            tv_about_us?.maxLines=Integer.MAX_VALUE
            group?.visibility=View.GONE
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



        viewModel.resultAboutUs.observe(viewLifecycleOwner, Observer {

            when (it.status) {
                Status.SUCCESS -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        tv_about_us?.text = Html.fromHtml(it.data?.data?.aboutUs, Html.FROM_HTML_MODE_LEGACY)
                    } else {
                        @Suppress("DEPRECATION")
                        tv_about_us?.text = Html.fromHtml(it.data?.data?.aboutUs)
                    }
                    group.visibility=View.VISIBLE
                }
                Status.FAILURE -> it.errorMsg?.let { showSnackBar(it) }
            }
        })

    }

}