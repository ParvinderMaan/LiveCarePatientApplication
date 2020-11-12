package com.app.patlivecare.medicalrecord.view

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.helper.Common
import com.app.patlivecare.helper.FileUtil
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.base.isUserInteractionEnabled
import com.app.patlivecare.medicalrecord.viewmodel.AddAttachmentAndReportViewModel
import com.app.patlivecare.network.WebHeader
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import androidx.lifecycle.Observer
import com.app.patlivecare.R
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.fragment_add_attachment_and_report.*
import kotlinx.android.synthetic.main.fragment_add_attachment_and_report.btn_save
import kotlinx.android.synthetic.main.fragment_add_attachment_and_report.cl_root
import kotlinx.android.synthetic.main.fragment_add_attachment_and_report.edt_desc
import kotlinx.android.synthetic.main.fragment_add_attachment_and_report.ibtn_close
import kotlinx.android.synthetic.main.fragment_add_attachment_and_report.toolbar_main
import kotlinx.android.synthetic.main.fragment_add_attachment_and_report.tv_header_title
import kotlinx.coroutines.*
import okhttp3.RequestBody
import java.util.*


class AddAttachmentAndReportFragment : BaseFragment() {
    private lateinit var nameRequestBody: RequestBody
    private lateinit var dateRequestBody: RequestBody
    private lateinit var descRequestBody: RequestBody
    private var filePath: String=""
    private var mFragmentListener: HomeFragmentSelectedListener?=null
    private var snackBarPermission: Snackbar? = null
    private val REQUEST_CODE_FILE: Int=210
    private lateinit var viewModel: AddAttachmentAndReportViewModel
    private var sharedPrefs: SharedPrefHelper? = null

    companion object {
        fun newInstance() = AddAttachmentAndReportFragment()
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
        val headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer "+accessToken
        viewModel = ViewModelProvider(this).get(AddAttachmentAndReportViewModel::class.java)
        viewModel.headerMap=headerMap
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_attachment_and_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_header_title?.text = getString(R.string.title_add_attachment_and_reports)
        if(activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(toolbar_main)

        initListener()
        initObserver()
    }

    private fun initListener() {
        ibtn_close?.setOnClickListener {
            mFragmentListener?.popTopMostFragment()
        }

        ll_banner?.setOnClickListener {}

        btn_save?.setOnClickListener {
            if(isFormValid()){
                val xXx = Common.prepareFilePart(filePath, "MedicalDocument")
                viewModel?.addAttachmentHistory(nameRequestBody,dateRequestBody,descRequestBody,xXx)
                }


        }

        edt_date?.setOnClickListener {
            edt_name?.clearFocus()
            showDatePicker()}

        tv_file_picker?.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 23) {
                Dexter.withActivity(activity)
                    .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(multiplePermissionsListener).check()
            }else{
                openGallery()
            }
        }

    }


    private fun isFormValid(): Boolean {
        val name = edt_name?.text.toString()
        val date = edt_date?.text.toString()
        val desc=edt_desc?.text.toString()
        val fileName=tv_file_name?.text.toString()
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(date) || TextUtils.isEmpty(desc) ||
            TextUtils.isEmpty(fileName) || TextUtils.isEmpty(filePath)){
            showSnackBar(getString(R.string.alert_please_complete_required_info))
            return false
        }
        nameRequestBody=Common.prepareTextPart(name)
        dateRequestBody=Common.prepareTextPart(date)
        descRequestBody=Common.prepareTextPart(desc)
        return true
    }



    /*     dd-MM-yyyy     */
    private fun showDatePicker() {
        val initYear: Int
        val initMonth: Int
        val initDay: Int
        val c = Calendar.getInstance()
        val selectedDob: String = edt_date?.text.toString()
        if (TextUtils.isEmpty(selectedDob)) {
            initYear = c[Calendar.YEAR]
            initMonth = c[Calendar.MONTH]
            initDay = c[Calendar.DAY_OF_MONTH]
        } else {
            val userSelected = selectedDob.split("-".toRegex()).toTypedArray()
            initYear = userSelected[2].toInt()
            initDay = userSelected[0].toInt()
            initMonth = userSelected[1].toInt() - 1
        }
        val datePickerDialog: DatePickerDialog
        datePickerDialog = DatePickerDialog(requireActivity(), R.style.PickerStyle,
            DatePickerDialog.OnDateSetListener { view1: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val dayOfMonthh = String.format(Locale.getDefault(), "%02d", dayOfMonth)
                val monthOfYearr = String.format(Locale.getDefault(), "%02d", monthOfYear + 1)
                val x = dayOfMonthh + "-" + monthOfYearr + "-" + year
                edt_date?.setText(x)

            },
            initYear,
            initMonth,
            initDay
        )
        datePickerDialog.datePicker.maxDate = c.timeInMillis
        datePickerDialog.setOnShowListener {
            val greenColor = ContextCompat.getColor(activity as Context, R.color.colorGreen)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(greenColor)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(greenColor)
        }
        datePickerDialog.show()


    }

    private var multiplePermissionsListener: MultiplePermissionsListener =object:
        MultiplePermissionsListener {
        override fun onPermissionsChecked(report: MultiplePermissionsReport) {
            if(report.areAllPermissionsGranted()){
                openGallery()
            }
            if(report.isAnyPermissionPermanentlyDenied){
                snackBarPermission= Snackbar.make(getRootView(), getString(R.string.title_camera_and_storage_access), Snackbar.LENGTH_INDEFINITE)
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





    private fun openGallery(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        val mimeTypes = arrayOf(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        )
        intent.type = "*/*"
        intent.type = "application/pdf|application/msword|application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        try {
            startActivityForResult(Intent.createChooser(intent, getString(R.string.alert_select_a_file)),REQUEST_CODE_FILE)
        } catch (ex: ActivityNotFoundException) {
            // Potentially direct the user to the Market with a Dialog
            showSnackBar(getString(R.string.alert_install_file_manager))
        }
}



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode ==REQUEST_CODE_FILE && data != null){
            val uri = data.data
            if (FileUtil.isGoogleDriveUri(uri))
                showSnackBar((getString(R.string.alert_download_google_drive_document)))
            else {
                 filePath= FileUtil.getRealPath(requireActivity(), uri)
                 tv_file_name?.text=filePath
                 tv_file_name.visibility=View.VISIBLE
            }
        }
    }

    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it){
                progress_bar?.show()
                btn_save?.visibility=View.INVISIBLE
            }
            else {
                progress_bar?.hide()
                btn_save?.visibility=View.VISIBLE
            }

        })

        viewModel.isViewEnable.observe(viewLifecycleOwner,Observer {
            if (it) view?.isUserInteractionEnabled(true)
            else view?.isUserInteractionEnabled(false)

        })

        viewModel.resultAddRecord.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS ->{
                    showSnackBar(it.data?.message.toString(),R.color.colorGreen)
                    mFragmentListener?.refreshUi(FragmentType.ATTACHMENT_AND_REPORT_FRAGMENT)
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(1200)
                        withContext(Dispatchers.Main) {
                            mFragmentListener?.popTopMostFragment()
                        }
                    }
                }
                Status.FAILURE -> showSnackBar(it.errorMsg.toString())
            }
        })

    }
}