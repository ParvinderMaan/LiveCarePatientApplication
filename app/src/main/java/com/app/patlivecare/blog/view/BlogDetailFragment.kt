package com.app.patlivecare.blog.view

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.Html
import android.transition.ChangeBounds
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.R
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.blog.model.BlogInfo
import com.app.patlivecare.blog.viewmodel.BlogDetailViewModel
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.helper.TimeUtil
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.network.WebHeader
import com.app.patlivecare.network.WebUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_blog_detail.*
import kotlinx.android.synthetic.main.fragment_blog_detail.toolbar_main
import java.util.HashMap

class BlogDetailFragment : BaseFragment() {
    private lateinit var headerMap: HashMap<String, String>
    private var blogInfo: BlogInfo?=null
    private lateinit var viewModel: BlogDetailViewModel
    private var mFragmentListener: HomeFragmentSelectedListener? = null
    private var sharedPrefs: SharedPrefHelper? = null


    companion object {
        fun newInstance(payload: Any?): BlogDetailFragment {
            val fragment = BlogDetailFragment()
            val bundle = Bundle()
            if (payload is Parcelable) bundle.putParcelable("KEY", payload)
            fragment.arguments = bundle
            return fragment
        }
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
        headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_CONTENT_TYPE] = WebHeader.VAL_CONTENT_TYPE
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer "+accessToken

        arguments?.let {
            blogInfo = it.getParcelable("KEY")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_blog_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(BlogDetailViewModel::class.java)
        super.onViewCreated(view, savedInstanceState)
        tv_header_title?.text = getString(R.string.title_blog_detail)
        if(activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(toolbar_main)

        ibtn_close?.setOnClickListener {
           activity?.finish()
        }

        tv_blog_title?.text=blogInfo?.title
        tv_doc_name?.text=blogInfo?.doctorName
        blogInfo?.createdDate?.let {
            tv_created_on?.text=TimeUtil.utcToLocal(it)
        }



        Glide.with(this)
            .load(WebUrl.BASE_FILE+blogInfo?.imgUrl)
            .centerCrop()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.color.colorLightestGrey)
            .into(iv_blog)

        Glide.with(this)
            .load(WebUrl.BASE_FILE+blogInfo?.doctorProfilePic)
            .centerCrop()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.color.colorLightestGrey)
            .into(iv_doctor)
        blogInfo?.id?.let {
            viewModel.fetchBlogDetail(headerMap,it)
        }

        initObserver()

    }


    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner,
            Observer {
                if (it) progress_bar?.visibility = View.VISIBLE
                else progress_bar?.visibility = View.INVISIBLE
            })

        viewModel.resultBlogDetail.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS ->{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        tv_blog_desc?.text = Html.fromHtml(it.data?.blogInfo?.description, Html.FROM_HTML_MODE_LEGACY)
                    } else {
                        @Suppress("DEPRECATION")
                        tv_blog_desc?.text = Html.fromHtml(it.data?.blogInfo?.description)
                    }

                }
                Status.FAILURE -> showSnackBar(it.errorMsg.toString())
            }
        })

    }

}