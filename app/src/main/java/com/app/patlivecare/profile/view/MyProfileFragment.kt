package com.app.patlivecare.profile.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider.getUriForFile
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.R
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.helper.Common
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.network.WebHeader
import com.app.patlivecare.network.WebUrl
import com.app.patlivecare.profile.viewmodel.MyProfileViewModel
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_my_profile.*
import java.io.File
import java.util.HashMap


class MyProfileFragment : BaseFragment() {
    private lateinit var headerMap: HashMap<String, String>
    private var fileName: String?=null
    private var snackBarPermission: Snackbar? = null
    private var setBitmapMaxWidthHeight:Boolean = false
    private val ASPECT_RATIO_X = 1
    private var ASPECT_RATIO_Y: Int = 1
    private var bitmapMaxWidth: Int = 1000
    private var bitmapMaxHeight: Int = 1000
    private val IMAGE_COMPRESSION = 80

    private lateinit var viewModel: MyProfileViewModel
    private val REQUEST_IMAGE_CAPTURE: Int=200
    private val REQUEST_GALLERY_IMAGE: Int=210

    private lateinit var basicFragment: ProfileBasicFragment
    private lateinit var additionalFragment: ProfileAdditionalFragment
    private var sharedPrefs: SharedPrefHelper? =null

    companion object {
        fun newInstance() = MyProfileFragment()
    }

    override fun getRootView(): View {
       return cl_root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        basicFragment = ProfileBasicFragment.newInstance()
        additionalFragment= ProfileAdditionalFragment.newInstance()

        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer "+accessToken


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyProfileViewModel::class.java)
        tv_basic.isSelected=true
        tv_additional.isSelected=false
        showBasicProfileFragment()

        tv_basic?.setOnClickListener {
            tv_basic.isSelected=true
            tv_additional.isSelected=false
            showBasicProfileFragment()
        }
        tv_additional?.setOnClickListener {
            tv_basic.isSelected=false
            tv_additional.isSelected=true
            showAdditionalProfileFragment()
        }
        initProfileHeader()

        initObserver()
    }

    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner,
            Observer {
                if (it) progress_bar_?.visibility = View.VISIBLE
                else progress_bar_?.visibility = View.INVISIBLE
            })
        viewModel.resultAlterProfilePic.observe(viewLifecycleOwner,
            Observer {
                when (it.status) {
                    Status.SUCCESS ->{
                        showSnackBar(it?.data?.message.toString(),R.color.colorGreen)
                        it?.data?.data?.profilepicurl?.let { it1 ->
                            sharedPrefs?.builder()?.write(SharedPrefHelper.KEY_PROFILE_PIC, it1)?.build()
                            val intent = Intent("event_refresh_navi_header")
                            LocalBroadcastManager.getInstance(activity as Context).sendBroadcast(intent) // Now!!!

                        }
                    }
                    Status.FAILURE -> showSnackBar(it.errorMsg.toString())
                }
            })
    }

    private fun showAdditionalProfileFragment() {
        val ft=childFragmentManager.beginTransaction()
        if (additionalFragment.isAdded) ft.show(additionalFragment)
        else ft.add(R.id.fl_container, additionalFragment)
        if (basicFragment.isAdded) ft.hide(basicFragment)
        ft.commit()
    }

    private fun showBasicProfileFragment() {
        val ft=childFragmentManager.beginTransaction()
        if (basicFragment.isAdded) ft.show(basicFragment)
        else ft.add(R.id.fl_container, basicFragment)
        if (additionalFragment.isAdded) ft.hide(additionalFragment)
        ft.commit()
    }

    private fun initProfileHeader() {
        val imgUrl = WebUrl.BASE_FILE+sharedPrefs?.read(SharedPrefHelper.KEY_PROFILE_PIC, "")
        civ_pat_pic?.let {
            Glide.with(this)
                .load(imgUrl)
                .centerCrop()
                .placeholder(R.drawable.img_avatar)
                .into(it)
        }

        tv_pat_name?.text=sharedPrefs?.read(SharedPrefHelper.KEY_FIRST_NAME,"")
            .plus(" ")
            .plus(sharedPrefs?.read(SharedPrefHelper.KEY_LAST_NAME,""))

        // Check storage permission first
        ibtn_edit_profile?.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 23) {
                Dexter.withActivity(activity)
                    .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(multiplePermissionsListener).check()
            }else{
                openImagePopupMenu()
            }
        }
    }





    private fun getCacheImagePath(): Uri? {
        val path = File(activity?.externalCacheDir, "camera")
        if (!path.exists()) path.mkdirs()
        val image = File(path, fileName)
        return activity?.let {
            getUriForFile(
                it,
                "com.app.patlivecare.provider",
                image
            )
        }
    }

    private fun openImagePopupMenu() {
        val popupMenu = PopupMenu(requireActivity(), ibtn_edit_profile)
        val menu = popupMenu.menu
        menu.add(0, 1, 0, getString(R.string.title_camera))
        menu.add(0, 2, 0, getString(R.string.title_gallery))
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
              when(item.itemId){
                  1 -> openCamera()
                  2 -> openGallery()

              }
            false
        }
        popupMenu.show()

    }





     private var multiplePermissionsListener: MultiplePermissionsListener=object:MultiplePermissionsListener{
         override fun onPermissionsChecked(report: MultiplePermissionsReport) {
             if(report.areAllPermissionsGranted()){
                 openImagePopupMenu()
             }
             if(report.isAnyPermissionPermanentlyDenied){
                 snackBarPermission=Snackbar.make(getRootView(), getString(R.string.title_camera_and_storage_access), Snackbar.LENGTH_INDEFINITE)
                     .setAction(getString(R.string.action_settings)) {
                     // open system setting screen ...
                     val intent = Intent()
                     intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                     val uri = Uri.fromParts("package", activity?.packageName, null)
                     intent.data = uri
                     activity?.startActivity(intent)
                 }
                 snackBarPermission?.show()
             }

         }

         override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken) {
             token.continuePermissionRequest()
         }
     }


    private fun openCamera(){
         fileName = "IMG_" + System.currentTimeMillis().toString() + ".jpg"
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath())
        if (activity?.packageManager?.let { intent.resolveActivity(it) } != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_GALLERY_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> if (resultCode == RESULT_OK) {
                cropImage(getCacheImagePath())
            }
            REQUEST_GALLERY_IMAGE -> if (resultCode == RESULT_OK) {
                val imageUri = data?.data
                cropImage(imageUri)
            }
            UCrop.REQUEST_CROP -> if (resultCode == RESULT_OK) {
                handleUCropResult(data)
            }
            UCrop.RESULT_ERROR -> {
                val cropError = UCrop.getError(data!!)
              //  Log.e("TAG", "Crop error: $cropError")
                showSnackBar("Crop error: $cropError")
            }
        }
    }

    private fun cropImage(sourceUri: Uri?) {
        val destinationUri = Uri.fromFile(File(activity?.cacheDir,
            queryName(activity?.contentResolver!!, sourceUri!!)))
        val options = UCrop.Options()
        options.setCompressionQuality(IMAGE_COMPRESSION)
        options.setToolbarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        options.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        options.setActiveWidgetColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        options.withAspectRatio(ASPECT_RATIO_X.toFloat(), ASPECT_RATIO_Y.toFloat())
        if (setBitmapMaxWidthHeight) options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight)
        UCrop.of(sourceUri!!, destinationUri).withOptions(options).start(requireActivity(), this)

    }

    private fun handleUCropResult(data: Intent?) {
        if (data == null) {
            return
        }
        val resultUri = UCrop.getOutput(data)
        Log.e("TAG", "Crop : $resultUri")
//         civ_pat_pic.setImageURI(resultUri)
        civ_pat_pic?.let {
            Glide.with(this)
                .load(resultUri)
                .centerCrop()
                .placeholder(R.drawable.img_avatar)
                .into(it)
        }
          resultUri?.path
          resultUri?.path?.let {
              var xXx = Common.prepareImagePart(it, "imgFile")
              viewModel.alterProfilePic(headerMap,xXx)
          }

    }

    private fun queryName(resolver: ContentResolver, uri: Uri): String? {
        val returnCursor: Cursor = resolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    override fun onDestroyView() {
        if(snackBarPermission!=null ){
            if(snackBarPermission!!.isShown){
                snackBarPermission?.dismiss()
            }
        }
        super.onDestroyView()

    }
}