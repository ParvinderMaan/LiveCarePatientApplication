package com.app.patlivecare.home.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.notification.view.NotificationFragment
import com.app.patlivecare.prescription.view.PrescriptionFragment
import com.app.patlivecare.R
import com.app.patlivecare.blog.view.BlogFragment
import com.app.patlivecare.consult.view.MyConsultFragment
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.medicalrecord.view.MedicalRecordFragment
import com.app.patlivecare.miscellaneous.view.AboutUsFragment
import com.app.patlivecare.miscellaneous.view.SettingFragment
import com.app.patlivecare.network.WebUrl
import com.app.patlivecare.profile.view.MyProfileFragment
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.*


class HomeActivity : AppCompatActivity() {
    private var tvPatAddress: TextView? = null
    private var ivPatPic: CircleImageView? = null
    var backPressedTwice: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_home)
        // ******** LAUNCHER *********
        // loggedIn ----> NO  ----> Splash,AuthenticationActivity
        //                YES ----> Splash,HomeActivity
        // ******** NOTIFICATION *********
        // loggedIn ----> YES ----> Splash,HomeActivity


        // first time.......
        initToolbar()
        initNavigationDrawer()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_container, HomeFragment.newInstance())
            addToBackStack(HomeFragment::class.simpleName)
            commit()
        }
        initNavigationHeader()

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(mMessageReceiver, IntentFilter("event_refresh_navi_header"))


        // IO,Main,Default
//        CoroutineScope(Dispatchers.Default).launch {
//            delay(5000)
//            withContext(Dispatchers.Main) {
//
//                val snackBar=Snackbar.make(drawer_layout, getString(R.string.title_complete_your_profile), Snackbar.LENGTH_INDEFINITE)
//                snackBar.view.setBackgroundColor(ContextCompat.getColor(this@HomeActivity,R.color.colorRed))
//                snackBar.setAction(getString(R.string.action_view)) {
//                    // open profile screen ...
//                    supportFragmentManager.beginTransaction().apply {
//                        replace(R.id.fl_container, MyProfileFragment.newInstance())
//                        addToBackStack(MyProfileFragment::class.simpleName)
//                        commit()
//                    }
//                    nav_view?.menu?.getItem(0)?.isChecked = true
//                    toolbar_main?.title = getString(R.string.menu_my_profile)
//                }.show()
//            }
//        }
    }

    private fun initNavigationHeader() {
        val sharedPrefs = (applicationContext as LiveCareApplication).getSharedPrefInstance()
        val headerView = nav_view?.getHeaderView(0)
        ivPatPic = headerView?.findViewById(R.id.iv_pat_pic)
        val tvPatName = headerView?.findViewById<TextView>(R.id.tv_pat_name)
        tvPatAddress = headerView?.findViewById<TextView>(R.id.tv_pat_address)
        val imgUrl = WebUrl.BASE_FILE + sharedPrefs.read(SharedPrefHelper.KEY_PROFILE_PIC, "")
        ivPatPic?.let {
            Glide.with(this)
                .load(imgUrl)
                .centerCrop()
                .placeholder(R.drawable.img_avatar)
                .into(it)
        }

        tvPatName?.text = sharedPrefs.read(SharedPrefHelper.KEY_FIRST_NAME, "")
            .plus(" ")
            .plus(sharedPrefs.read(SharedPrefHelper.KEY_LAST_NAME, ""))

        val addr = sharedPrefs.read(SharedPrefHelper.KEY_ADDRESS, "")
        val add = addr?.split("|")
        tvPatAddress?.text = add?.get(0)
            .plus(" ")
            .plus(add?.get(2))

    }

    private fun initToolbar() {
        toolbar_main?.title = getString(R.string.menu_home)
        setSupportActionBar(toolbar_main)
    }

    private fun initNavigationDrawer() {
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar_main, 0, 0)
        drawer_layout?.addDrawerListener(toggle)
        toggle.syncState()
        nav_view?.setNavigationItemSelectedListener { item ->
            if (item.isChecked) {
                return@setNavigationItemSelectedListener true
            }
            selectItem(item)
            nav_view?.setCheckedItem(item)
            // IO,Main,Default
            CoroutineScope(Dispatchers.Default).launch {
                delay(300)
                withContext(Dispatchers.Main) {
                    drawer_layout?.closeDrawer(GravityCompat.START)
                }
            }

            true
        }
    }

    private fun selectItem(item: MenuItem) {
        var fragment: Fragment? = null
        when (item.itemId) {
            R.id.nav_home -> {
                toolbar_main?.title = getString(R.string.menu_home)
                fragment = HomeFragment.newInstance()
            }
            R.id.nav_my_consults -> {
                toolbar_main?.title = getString(R.string.menu_my_consults)
                fragment = MyConsultFragment.newInstance()
            }
            R.id.nav_my_medical_records -> {
                toolbar_main?.title = getString(R.string.menu_my_medical_records)
                fragment = MedicalRecordFragment.newInstance()
            }
            R.id.nav_blogs -> {
                toolbar_main?.title = getString(R.string.menu_blogs)
                fragment = BlogFragment.newInstance()
            }
            R.id.nav_my_profile -> {
                toolbar_main?.title = getString(R.string.menu_my_profile)
                fragment = MyProfileFragment.newInstance()
            }
            R.id.nav_prescription -> {
                toolbar_main?.title = getString(R.string.menu_prescription)
                fragment = PrescriptionFragment.newInstance()
            }
            R.id.nav_notifications -> {
                toolbar_main?.title = getString(R.string.menu_notifications)
                fragment = NotificationFragment.newInstance()
            }
            R.id.nav_settings -> {
                toolbar_main?.title = getString(R.string.menu_settings)
                fragment = SettingFragment.newInstance()
            }
            R.id.nav_about_us -> {
                toolbar_main?.title = getString(R.string.menu_about_us)
                fragment = AboutUsFragment.newInstance()
            }

        }
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.fl_container, fragment!!)  // #
                    .addToBackStack(fragment::class.simpleName)
                commit()
            }

    }

    override fun onBackPressed() {
        //   onBackPressDoubleClick()
        val fr: Fragment = supportFragmentManager.findFragmentById(R.id.fl_container) ?: return
        if (fr.javaClass.simpleName.equals("HomeFragment")) {
            onBackPressDoubleClick()
        } else {
            supportFragmentManager.popBackStack("HomeFragment", 0)
            nav_view?.menu?.getItem(0)?.isChecked = true
            toolbar_main?.title = getString(R.string.menu_home)
        }
    }

    private fun onBackPressDoubleClick() {
        if (backPressedTwice) finish()

        backPressedTwice = true;
        Toast.makeText(this, getString(R.string.alert_back_press), Toast.LENGTH_SHORT).show();
        // IO,Main,Default
        CoroutineScope(Dispatchers.Default).launch {
            delay(2000)
            withContext(Dispatchers.Main) {
                backPressedTwice = false
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(mMessageReceiver)

        Glide.get(this).clearMemory()
    }


    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val sharedPrefs = (applicationContext as LiveCareApplication).getSharedPrefInstance()
            val imgUrl = WebUrl.BASE_FILE + sharedPrefs.read(SharedPrefHelper.KEY_PROFILE_PIC, "")
            ivPatPic?.let {
                Glide.with(this@HomeActivity)
                    .load(imgUrl)
                    .centerCrop()
                    .placeholder(R.drawable.img_avatar)
                    .into(it)
            }

            val addr = sharedPrefs.read(SharedPrefHelper.KEY_ADDRESS, "")
            val add = addr?.split("|")
            tvPatAddress?.text = add?.get(0)
                .plus(" ")
                .plus(add?.get(2))

        }
    }

    fun showProfileFragment() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_container, MyProfileFragment.newInstance())
            addToBackStack(MyProfileFragment::class.simpleName)
            commit()
        }
        nav_view?.menu?.getItem(4)?.isChecked = true
        toolbar_main?.title = getString(R.string.menu_my_profile)
    }
}