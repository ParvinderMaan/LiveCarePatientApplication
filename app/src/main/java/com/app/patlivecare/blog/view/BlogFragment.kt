package com.app.patlivecare.blog.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.MinorActivity
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.blog.model.BlogInfo
import com.app.patlivecare.blog.viewmodel.BlogViewModel
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.medicalrecord.viewmodel.PastMedicalHistoryViewModel
import com.app.patlivecare.network.WebHeader
import kotlinx.android.synthetic.main.fragment_blog.*
import androidx.lifecycle.Observer
import java.util.*


class BlogFragment : Fragment() {
    private lateinit var viewModel: BlogViewModel
    private  var blogAdapter: BlogAdapter?=null
    private var sharedPrefs: SharedPrefHelper? = null

    companion object {
        fun newInstance() = BlogFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        val headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_ACCEPT] = WebHeader.VAL_ACCEPT
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer "+accessToken
        viewModel = ViewModelProvider(this).get(BlogViewModel::class.java)
        viewModel.headers= headerMap
        blogAdapter = BlogAdapter(Date())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchBlogs()

        rv_blog?.apply {
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            adapter = blogAdapter
        }
        blogAdapter?.setOnItemClickListener(object : BlogAdapter.OnItemClickListener{
            override fun onItemClick(item: BlogInfo) {
                val intent= Intent(activity, MinorActivity::class.java)
                intent.putExtra("fragment_type", FragmentType.BLOG_DETAIL_FRAGMENT)
                intent.putExtra("key_",item)
                startActivity(intent)
            }
        })

        initObserver()

    }
    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner,
            Observer {
                if (it) progress_bar?.show()
                else progress_bar?.hide()
            })


        viewModel.isListEmpty.observe(viewLifecycleOwner,
            Observer {
                if (it) group_empty_view?.visibility = View.VISIBLE })



        viewModel.userPagedList.observe(viewLifecycleOwner, Observer {
            blogAdapter?.submitList(it)
        })
    }

}