package com.app.patlivecare.videocall.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.R
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.consult.model.AppointmentInfo
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.helper.TimeUtil
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.network.WebHeader
import com.app.patlivecare.network.WebUrl
import com.app.patlivecare.videocall.viewmodel.WaitingRoomViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_waiting_room.*
import kotlinx.android.synthetic.main.fragment_waiting_room.ibtn_close
import kotlinx.android.synthetic.main.fragment_waiting_room.iv_profile
import kotlinx.android.synthetic.main.fragment_waiting_room.tv_doc_name
import kotlinx.android.synthetic.main.list_item_upcoming_consult.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.ZonedDateTime
import java.util.HashMap


class WaitingRoomFragment : BaseFragment() {
    private lateinit var viewModel: WaitingRoomViewModel
    private var appointmentInfo: AppointmentInfo? = null
    private var mFragmentListener: HomeFragmentSelectedListener? = null
    private  var sharedPrefs: SharedPrefHelper? = null
    private lateinit var todayZonedDateTime: ZonedDateTime
    var timer:CountDownTimer?=null

    companion object {
        fun newInstance(payload: Any?): WaitingRoomFragment {
            val fragment = WaitingRoomFragment()
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
        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        arguments?.let {
            appointmentInfo = it.getParcelable("KEY")
        }

        val headerMap = HashMap<String,String>()
        headerMap[WebHeader.KEY_ACCEPT] = WebHeader.VAL_ACCEPT
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer " + accessToken
        viewModel = ViewModelProvider(this).get(WaitingRoomViewModel::class.java)
        viewModel.headerMap=headerMap
        todayZonedDateTime= ZonedDateTime.now()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_waiting_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        fbtn_join_call?.hide() // use hide() in production...

        appointmentInfo?.remainingTime?.let {
            if (it == -1L) {
                fbtn_join_call?.show()
                tv_remaining_time?.text ="Call in Progress.."
            }else{
                // timer
                if (timer == null) {
                    timer = object : CountDownTimer(it, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            tv_remaining_time?.text = TimeUtil.getTimeSpan(millisUntilFinished)
                        }
                        override fun onFinish() {
                            timer == null
                            tv_remaining_time?.text ="Call in Progress.."
                            fbtn_join_call?.show()
                        }
                    }.start()
                }
            }

        }
        appointmentInfo?.profilePic.let {
            if (!it.isNullOrEmpty()) {
                Glide.with(this)
                    .load(WebUrl.BASE_FILE + it)
                    .centerCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.colorLightestGrey)
                    .into(iv_profile)
            } else {
                Glide.with(this)
                    .load(R.drawable.img_avatar)
                    .centerCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.colorLightestGrey)
                    .into(iv_profile)

                iv_profile.setBackgroundColor(ContextCompat.getColor(activity as Activity,R.color.colorLightBlack))
            }
        }

       if(appointmentInfo?.firstName !=null && appointmentInfo?.lastName !=null){
           val fullDocName = getString(R.string.title_dr)
               .plus(" ")
               .plus(appointmentInfo?.firstName)
               .plus(" ")
               .plus(appointmentInfo?.lastName)
             tv_doc_name?.text=fullDocName
       }

        initObserver()
    }

    private fun initListener() {
        fbtn_join_call?.setOnClickListener {
//            val intent= Intent(activity, VideoCallActivity::class.java)
//            startActivity(intent)
//            showSnackBar("Working in progress",R.color.colorGreen)
//            activity?.finish()
            appointmentInfo?.appointmentId?.let {
                    id -> viewModel.fetchVideoToken(id) }
        }

        ibtn_close?.setOnClickListener {
            activity?.finish()
        }

    }


    override fun onDestroyView() {
        if (timer != null ){
            timer?.cancel()
        }
        super.onDestroyView()
    }

    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it){
                progress_bar?.visibility = View.VISIBLE
                fbtn_join_call?.visibility = View.INVISIBLE
            } else{
                progress_bar?.visibility = View.INVISIBLE
                fbtn_join_call?.visibility = View.VISIBLE
            }
        })

        viewModel.resultVideoCallToken.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                     it.data?.data?.let {
                     val intent= Intent(activity, VideoCallActivity::class.java)
                     intent.putExtra("KEY_",it)
                     startActivity(intent) // for now.
                 //  activity?.finish()
                    }
                }
                Status.FAILURE -> {
                    showSnackBar(it.errorMsg.toString())
                }
            }
        })
    }

}