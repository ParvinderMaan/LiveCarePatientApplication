package com.app.patlivecare.medicalrecord.view

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.patlivecare.R
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.medicalrecord.viewmodel.ReportViewerViewModel
import com.app.patlivecare.network.WebUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.fragment_report_viewer.*

class ReportViewerFragment : Fragment() {
    private lateinit var viewModel: ReportViewerViewModel
    private var medicalDocumentUrl: String? = null
    private var mFragmentListener: HomeFragmentSelectedListener? = null

    companion object {
        fun newInstance(payload: Any?): ReportViewerFragment {
            val fragment = ReportViewerFragment()
            val bundle = Bundle()
            if (payload is String) bundle.putString("KEY", payload)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeFragmentSelectedListener) mFragmentListener = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            medicalDocumentUrl = it.getString("KEY")
        }
        Log.e("url: ", medicalDocumentUrl)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report_viewer, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(ReportViewerViewModel::class.java)
        super.onViewCreated(view, savedInstanceState)
        tv_header_title?.text = getString(R.string.title_report)
        if(activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(toolbar_main)
        ibtn_close?.setOnClickListener {
            mFragmentListener?.popTopMostFragment()
        }

        initObserver()

        if (medicalDocumentUrl!!.contains(".pdf") || medicalDocumentUrl!!.contains(".PDF")
            || medicalDocumentUrl!!.contains(".doc") || medicalDocumentUrl!!.contains(".docx")) {
            web_view?.visibility = View.VISIBLE
            web_view?.settings?.javaScriptEnabled = true
            web_view?.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    view?.loadUrl(url)
                    return true
                }
            }
            web_view?.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, progress: Int) {
                    when {
                        progress < 100 -> viewModel.isLoading.value = true
                        progress == 100 -> viewModel.isLoading.value = false
                    }
                }


            }

            web_view?.clearHistory()
            web_view?.clearFormData()
            web_view?.clearCache(true)
            web_view?.loadUrl("https://docs.google.com/viewer?embedded=true&url=" + WebUrl.BASE_FILE + medicalDocumentUrl)

            return
        }


        if (medicalDocumentUrl!!.contains(".JPEG") || medicalDocumentUrl!!.contains(".jpg") || medicalDocumentUrl!!.contains(
                ".JPG")
            || medicalDocumentUrl!!.contains(".png") || medicalDocumentUrl!!.contains(".PNG")
        ) {
            iv_report?.visibility = View.VISIBLE
            viewModel.isLoading.value = true
            iv_report?.let {
                Glide.with(this)
                    .load(WebUrl.BASE_FILE + medicalDocumentUrl)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            viewModel.isLoading.value = false
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            viewModel.isLoading.value = false
                            return false
                        }

                    })

                    .into(it)
            }





            return
        }

    }

    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) progress_bar?.show()
            else progress_bar?.hide()
        })
    }


}