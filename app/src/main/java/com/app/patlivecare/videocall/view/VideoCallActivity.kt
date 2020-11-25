package com.app.patlivecare.videocall.view

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.patlivecare.extra.MinorActivity
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.videocall.model.VideoTokenResponse
import com.app.patlivecare.videocall.viewmodel.VideoCallViewModel
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.opentok.android.*
import com.opentok.android.Session.ConnectionListener
import kotlinx.android.synthetic.main.activity_video_call.*
import kotlinx.android.synthetic.main.activity_video_call.cl_root


class VideoCallActivity : AppCompatActivity() {
    private lateinit var viewModel: VideoCallViewModel
    private var videoCallInfo: VideoTokenResponse.Data?=null
    private var session: Session? = null
    private var publisher: Publisher? = null
    private var subscriber: Subscriber? = null
    private var snackBarPermission: Snackbar? = null

    companion object {
        // default
        private  var API_KEY = "46927294"
        private  var SESSION_ID ="1_MX40NjkyNzI5NH5-MTYwNDU1MzcwNTU1Nn4rWnFRaWI1TG9aY2dMOHlYRTFxR3Vjc3V-fg"
        private  var TOKEN = "T1==cGFydG5lcl9pZD00NjkyNzI5NCZzaWc9ZTA1YzgyZDAzZjAwYTJiM2IwYWJmMDk4M2NhMDk5NTNmYWRiOThkYjpzZXNzaW9uX2lkPTFfTVg0ME5qa3lOekk1Tkg1LU1UWXdORFUxTXpjd05UVTFObjRyV25GUmFXSTFURzlhWTJkTU9IbFlSVEZ4UjNWamMzVi1mZyZjcmVhdGVfdGltZT0xNjA0NTUzNzI5Jm5vbmNlPTAuNDg0MDEzNTM0MjMzOTEwMjUmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTYwNzE0NTcyOCZpbml0aWFsX2xheW91dF9jbGFzc19saXN0PQ=="
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_video_call)
        viewModel = ViewModelProvider(this).get(VideoCallViewModel::class.java)
        // collect our intent
        if (intent != null) {
            videoCallInfo = intent.getParcelableExtra("KEY_")
            if (videoCallInfo != null ) {
                API_KEY = videoCallInfo?.openTokApiKey.toString()
                SESSION_ID = videoCallInfo?.mediaValue?.sessionId.toString()
                TOKEN = videoCallInfo?.mediaValue?.token.toString()
            }
        }
        // check permissions
        if (Build.VERSION.SDK_INT >= 23) {
            Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                .withListener(multiplePermissionsListener).check()
        }else{
            viewModel.allPermGranted.value=true
        }

        initListener()
        initObserver()
    }

    private fun initObserver() {
        viewModel.showProgress.observe(this, Observer {
                if (it) progress_bar?.visibility = View.VISIBLE
                else progress_bar?.visibility = View.INVISIBLE
            })

        viewModel.allPermGranted.observe(this, Observer {
            if(it){
                initTokBoxSession()
                initiateCall()
               } else{
                   showPermissionSnackBar()
               }
        })


        viewModel.isAudioPublished.observe(this, Observer {
            if (publisher == null) return@Observer

               if (publisher!!.publishAudio) {
                    publisher?.publishAudio = false
                    iv_switch_voice?.setImageResource(R.drawable.ic_mute)
                } else {
                    publisher?.publishAudio = true
                    iv_switch_voice?.setImageResource(R.drawable.ic_mute_)
                }

        })


        viewModel.isDoctorArrived.observe(this, Observer {
          //  if (subscriber == null) return@Observer

            if (it) {
                tv_waiting_prompt?.visibility=View.VISIBLE
              //  group_controls?.visibility=View.VISIBLE
            } else {
                tv_waiting_prompt?.visibility=View.INVISIBLE
              //  group_controls?.visibility=View.INVISIBLE
            }
        })
    }

    private fun initListener() {
        iv_start_end_call?.setOnClickListener {

           // finish()
            if (session == null) return@setOnClickListener
            // by default
            initiateCall()

        }

        iv_switch_voice?.setOnClickListener {
            viewModel.isAudioPublished.value=true
        }

        iv_medical_record?.setOnClickListener {
          // open medical records ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            val intent= Intent(this, MinorActivity::class.java)
            intent.putExtra("fragment_type", FragmentType.MEDICAL_RECORD_FRAGMENT)
            startActivity(intent)
        }

        fl_publisher_container?.setOnClickListener {

        }

        iv_switch_camera?.setOnClickListener {
            if (publisher != null)
                publisher?.swapCamera()
        }
    }

    private fun initiateCall() {
        if (!session!!.isSessionConnected) {
            session?.connect(TOKEN)
//                iv_start_end_call?.setImageResource(R.drawable.ic_call_end)
        } else {
            session?.disconnect()
//                iv_start_end_call?.setImageResource(R.drawable.ic_call_start)

        }

    }

    private fun initTokBoxSession() {
        if(session!=null) return
        // initialize and connect to the session
        viewModel.showProgress.value=true
        session =  Session.Builder(this, API_KEY, SESSION_ID).build()
        session?.setSessionListener(sessionListener)  // one client
        session?.setConnectionListener(connectionListener) // other client
        session?.setReconnectionListener(reconnectionListener)
        session?.setStreamPropertiesListener(streamPropertiesListener)
    }

    private fun initPublisher() {
        if (publisher == null) {
            publisher = Publisher.Builder(this).build()
            publisher?.setPublisherListener(publisherListener)
            publisher?.getRenderer()?.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL)
            fl_publisher_container?.addView(publisher?.view)
            publisher?.startPreview()
        }
    }
    private fun initSubscriber(stream: Stream) {
        if (subscriber == null) {
            subscriber = Subscriber.Builder(this@VideoCallActivity, stream).build()
            subscriber?.setSubscriberListener(subscriberListener)
            session?.subscribe(subscriber)
            if (subscriber?.view is GLSurfaceView) {
                (subscriber?.view as GLSurfaceView).setZOrderOnTop(false)
            }
            subscriber?.getRenderer()?.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL)
            fl_subscriber_container?.addView(subscriber?.view)

        }

    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
private var connectionListener: ConnectionListener? = object : ConnectionListener {
    override fun onConnectionCreated(session: Session, connection: Connection) {
         viewModel.isDoctorArrived.value=true

    }
    override fun onConnectionDestroyed(session: Session, connection: Connection) {
        viewModel.isDoctorArrived.value=false
    }
}

private var subscriberListener:SubscriberKit.SubscriberListener = object :  SubscriberKit.SubscriberListener{
    override fun onConnected(p0: SubscriberKit?) {
        Log.e("TAG", "Subscriber onConnected")

    }

    override fun onDisconnected(p0: SubscriberKit?) {
        Log.e("TAG", "Subscriber onDisconnected")

    }

    override fun onError(p0: SubscriberKit?, p1: OpentokError?) {
        Log.e("TAG", "Subscriber onError"+p1?.message)

    }


}
    private var reconnectionListener = object : Session.ReconnectionListener{
        override fun onReconnecting(p0: Session?) {
            viewModel.showProgress.value=true
        }

        override fun onReconnected(p0: Session?) {
            viewModel.showProgress.value=false
        }
    }

    private var  streamPropertiesListener :Session.StreamPropertiesListener = object : Session.StreamPropertiesListener {
        override fun onStreamHasAudioChanged(p0: Session?, p1: Stream?, hasAudio: Boolean) {
            if (hasAudio) {
                iv_switch_voice?.setImageResource(R.drawable.ic_mute_)
            } else {
                iv_switch_voice?.setImageResource(R.drawable.ic_mute)
            }
        }

        override fun onStreamHasVideoChanged(p0: Session?, p1: Stream?, p2: Boolean) {}

        override fun onStreamVideoDimensionsChanged(p0: Session?, p1: Stream?, p2: Int, p3: Int) {}

        override fun onStreamVideoTypeChanged(p0: Session?, p1: Stream?, p2: Stream.StreamVideoType?) {

        }
    }
    private var publisherListener:PublisherKit.PublisherListener =object : PublisherKit.PublisherListener {
        override fun onStreamCreated(p0: PublisherKit?, p1: Stream?) {
            Log.i("TAG", "Publisher onStreamCreated")

        }

        override fun onStreamDestroyed(p0: PublisherKit?, p1: Stream?) {
            Log.i("TAG", "Publisher onStreamDestroyed")
        }

        override fun onError(p0: PublisherKit?, opentokError: OpentokError?) {
            Log.e("TAG", "Publisher error: " + opentokError?.message)
        }

    }

private var  sessionListener:Session.SessionListener? = object :  Session.SessionListener {
    override fun onConnected(p0: Session?) {
        Log.e("TAG", "SessionListener onConnected : ")
        initPublisher()
        session?.publish(publisher)

        if(p0!!.isSessionConnected) viewModel.showProgress.value=false
        if(p0.isSessionConnected)  iv_start_end_call?.setImageResource(R.drawable.ic_call_end)
        else  iv_start_end_call?.setImageResource(R.drawable.ic_call_start)
        group_surface?.visibility=View.VISIBLE
        group_controls?.visibility=View.VISIBLE
        // timer is missing ...........
        //tv_call_duration
    }
    override fun onDisconnected(p0: Session?) {
        Log.e("TAG", "SessionListener onDisconnected : ")
        if(!p0!!.isSessionConnected) viewModel.showProgress.value=false
        if(p0!!.isSessionConnected)  iv_start_end_call?.setImageResource(R.drawable.ic_call_end)
        else  iv_start_end_call?.setImageResource(R.drawable.ic_call_start)
        group_surface?.visibility=View.INVISIBLE
        group_controls?.visibility=View.INVISIBLE
    }

    override fun onStreamReceived(p0: Session?, stream: Stream) {
        Log.e("TAG", "SessionListener onStreamReceived : ")
        initSubscriber(stream)

    }
    override fun onStreamDropped(p0: Session?, p1: Stream?) {
        Log.e("TAG", "SessionListener onStreamDropped : ")
        removeSubscriber()
    }
    override fun onError(p0: Session?, opentokError: OpentokError?) {
        Log.e("TAG", "Session error: " + opentokError?.message);
        Log.e("TAG", "Session error code: " + opentokError?.errorCode);
        viewModel.showProgress.value=false
    }
}

    private fun removeSubscriber() {
        if (subscriber != null) {
            subscriber = null
            fl_subscriber_container?.removeAllViews()
        }
    }

    private var multiplePermissionsListener: MultiplePermissionsListener =object:
        MultiplePermissionsListener {
        override fun onPermissionsChecked(report: MultiplePermissionsReport) {
            if(report.areAllPermissionsGranted()){
                viewModel.allPermGranted.value=true
            }
            if(!report.areAllPermissionsGranted()){
                viewModel.allPermGranted.value=false
            }
            if(report.isAnyPermissionPermanentlyDenied){
                viewModel.allPermGranted.value=false
            }
        }
        override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken) {
            token.continuePermissionRequest()
        }
    }

    private fun showPermissionSnackBar() {
        snackBarPermission= Snackbar.make(cl_root, getString(R.string.title_camera_and_audio_access), Snackbar.LENGTH_INDEFINITE)
        snackBarPermission?.view?.setBackgroundColor(ContextCompat.getColor(this,R.color.colorTurquoise))
        snackBarPermission?.setAction(getString(R.string.action_settings)) {
              //  viewModel.isDeviceSettingVisited=true
                // open system setting screen ...
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
                finish()
            }
        snackBarPermission?.show()
    }


    override fun onResume() {
        super.onResume()
        if (session != null) {
            session?.onResume()
        }

//        if (viewModel.isDeviceSettingVisited) {
//
//            if(viewModel.allPermStatus.value!="ALLOWED"){
//                Dexter.withActivity(this)
//                    .withPermissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
//                    .withListener(multiplePermissionsListener).check()
//            }
//
//        }
    }

    override fun onPause() {
        super.onPause()
        if (session != null) {
            session?.onPause()
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()

    }
}




/*
window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
 */