package com.app.patlivecare.rating.view

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.R
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.base.BaseTextWatcher
import com.app.patlivecare.consult.model.AppointmentInfo
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.network.WebHeader
import com.app.patlivecare.rating.viewmodel.AddDoctorReviewViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_add_doctor_review.*
import kotlinx.android.synthetic.main.fragment_add_doctor_review.ibtn_close
import kotlinx.android.synthetic.main.fragment_add_doctor_review.toolbar_main
import kotlinx.android.synthetic.main.fragment_add_doctor_review.tv_header_title
import java.util.HashMap


class AddDoctorReviewFragment : BaseFragment() {
    private var mFragmentListener: HomeFragmentSelectedListener? = null
    private lateinit var viewModel: AddDoctorReviewViewModel
    private var sharedPrefs: SharedPrefHelper? = null
    private var appointmentInfo: AppointmentInfo? = null
    private var snackBarSuccess: Snackbar?=null

    companion object {
        fun newInstance(payload: Any?): AddDoctorReviewFragment {
            val fragment = AddDoctorReviewFragment()
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
        if (context is HomeFragmentSelectedListener) mFragmentListener = context
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            appointmentInfo = it.getParcelable("KEY")
        }
        viewModel = ViewModelProvider(this).get(AddDoctorReviewViewModel::class.java)
        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        val headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_CONTENT_TYPE] = WebHeader.VAL_CONTENT_TYPE
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer " + accessToken
        viewModel.headerMap = headerMap
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_doctor_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_header_title?.text = getString(R.string.title_add_user_review)
        if(activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(toolbar_main)

        ibtn_close?.setOnClickListener {
            activity?.finish()
        }



        tv_review?.addTextChangedListener(object : BaseTextWatcher() {
            override fun onTextChanged(start: Int, before: Int, count: Int, s: CharSequence?) {
             if(s==null)   return
                if(s.isNotEmpty()) {
                    tv_review_limit?.text=s.length.toString().plus("/").plus("200") // maxlimit
                    viewModel.isPostButtonVisible.value=true
                }else{
                    tv_review_limit?.text="0".plus("/").plus("200") // maxlimit
                    viewModel.isPostButtonVisible.value=false
                }
            }
        })
        btn_post.setOnClickListener {
            appointmentInfo?.appointmentId?.let {
                hideKeyboard(tv_review)
                val userReview = tv_review.text.toString()
                viewModel.addDoctorRatingAndReview(it,userReview)
            }

        }

        tv_review?.setOnEditorActionListener { v, actionId, event ->
            if (actionId== EditorInfo.IME_ACTION_GO) {
                appointmentInfo?.appointmentId?.let {
                    val userReview = tv_review.text.toString()
                    viewModel.addDoctorRatingAndReview(it,userReview)
                }
            }
            true
        }

        initObserver()
    }


    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner,
            Observer {
                if (it) progress_bar?.show()
                else progress_bar?.hide()
                if (it) btn_post?.visibility=View.INVISIBLE
                else btn_post?.visibility=View.VISIBLE
            })

        viewModel.resultAddDoctorReview.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.message?.let {
                        showSnackBar(it)
                        btn_post?.visibility=View.INVISIBLE
                        tv_review.setText("")
                        tv_review_limit.text = "0/200"
                    }
                }
                Status.FAILURE -> {
                    super.showSnackBar(it.errorMsg.toString(),R.color.colorRed)
                }
            }
        })

        viewModel.isPostButtonVisible.observe(viewLifecycleOwner,Observer {
                if (it) { if(btn_post?.visibility==View.INVISIBLE) btn_post?.visibility=View.VISIBLE
                } else btn_post?.visibility=View.INVISIBLE
            })
    }
    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showSnackBar(msg:String){
        snackBarSuccess = Snackbar.make(getRootView(), msg, Snackbar.LENGTH_INDEFINITE)
        snackBarSuccess?.view?.setBackgroundColor(ContextCompat.getColor(requireActivity(),R.color.colorGreen))
        snackBarSuccess?.setActionTextColor(Color.WHITE)
        snackBarSuccess?.setAction(getString(R.string.action_ok)) {
            activity?.finish()
        }
            ?.show()
    }
}