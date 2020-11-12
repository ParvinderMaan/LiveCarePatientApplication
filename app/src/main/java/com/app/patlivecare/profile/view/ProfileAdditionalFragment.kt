package com.app.patlivecare.profile.view

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.R
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.network.WebHeader
import com.app.patlivecare.profile.model.AdditionalInfoResponse
import com.app.patlivecare.profile.model.AdditionalProfileInfo
import com.app.patlivecare.profile.viewmodel.ProfileAdditionalViewModel
import kotlinx.android.synthetic.main.fragment_profile_additional.*
import kotlinx.android.synthetic.main.fragment_sign_up_four.*
import java.util.*


class ProfileAdditionalFragment : BaseFragment() {
    private lateinit var viewModel: ProfileAdditionalViewModel
    private var sharedPrefs: SharedPrefHelper? = null
    private lateinit var accessToken: String

    companion object {
        fun newInstance() = ProfileAdditionalFragment()
    }

    override fun getRootView(): View {
        return fl_root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        val headerMap = HashMap<String, String>()
        headerMap.put(WebHeader.KEY_CONTENT_TYPE, WebHeader.VAL_CONTENT_TYPE)
        headerMap.put(WebHeader.KEY_AUTHORIZATION, "Bearer " + accessToken)
        viewModel = ViewModelProvider(this).get(ProfileAdditionalViewModel::class.java)
        viewModel.headerMap = headerMap
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_additional, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchAdditionalInfo()
        initListener()
        initObserver()

    }

    private fun initListener() {
        btn_edit.tag = 0
        btn_edit?.setOnClickListener {
            when (btn_edit.tag) {
                0 -> {
                    btn_edit.tag = 1
                    btn_edit.text = getString(R.string.action_save)
                    setViewEnable(true)
                }
                1 -> {
                    if(isFormValid()) viewModel.updateAdditionalProfileInfo()
//                    btn_edit.tag=0
//                    btn_edit.text=getString(R.string.action_edit)
//                    setViewEnable(false)
                }
            }
        }

        edt_height?.setOnClickListener {
            val alertDialogFragment: UnitConvertorDialogFragment =
                UnitConvertorDialogFragment.newInstance("HEIGHT")
            alertDialogFragment.setOnHeightDialogListener(object :
                UnitConvertorDialogFragment.HeightDialogListener {
                override fun onClickHeight(height: Double) {
                    val formatHeight = String.format(Locale.getDefault(), "%.2f", height)
                    edt_height.setText(formatHeight)
                    edt_height.append(" ")
                    edt_height.append("cms")
                    alertDialogFragment.dismiss()
                    viewModel.additionalProInfo.value?.height = formatHeight.toFloat()
                }
            })
            alertDialogFragment.show(childFragmentManager, "TAG")

        }

        edt_weight?.setOnClickListener {
            val alertDialogFragment: UnitConvertorDialogFragment =
                UnitConvertorDialogFragment.newInstance("WEIGHT")
            alertDialogFragment.setOnWeightDialogListener(object :
                UnitConvertorDialogFragment.WeightDialogListener {
                override fun onClickWeight(weight: Double) {
                    var formatWeight = String.format(Locale.getDefault(), "%.2f", weight)
                    edt_weight.setText(formatWeight)
                    edt_weight.append(" ")
                    edt_weight.append("lbs")
                    alertDialogFragment.dismiss()
                    viewModel.additionalProInfo.value?.weight = formatWeight.toFloat()
                }
            })
            alertDialogFragment.show(childFragmentManager, "TAG")
        }



        edt_blood_group?.setOnClickListener {
            showBloodGroupPopupMenu()
        }

        edt_vegetarian?.setOnClickListener {
            showAddictionPopupMenu(edt_vegetarian)
        }

        edt_smoke?.setOnClickListener {
            showAddictionPopupMenu(edt_smoke)
        }

        edt_alcohol?.setOnClickListener {
            showAddictionPopupMenu(edt_alcohol)
        }

        edt_drug?.setOnClickListener {
            showAddictionPopupMenu(edt_drug)
        }
    }

    private fun isFormValid(): Boolean {
        val height = edt_height.text.toString()
        val weight = edt_weight.text.toString()
        val bloodGroup = edt_blood_group.text.toString()
        val vegetarian = edt_vegetarian.text.toString()
        val smoke = edt_smoke.text.toString()
        val alcohol = edt_alcohol.text.toString()
        val drug = edt_drug.text.toString()

        if (TextUtils.isEmpty(height) || TextUtils.isEmpty(weight) || TextUtils.isEmpty(bloodGroup)
            || TextUtils.isEmpty(vegetarian) || TextUtils.isEmpty(smoke) || TextUtils.isEmpty(alcohol)
            || TextUtils.isEmpty(drug)) {
            showSnackBar(getString(R.string.alert_please_complete_required_info))
            return false
        }

        return true
    }


    private fun showBloodGroupPopupMenu() {
        val popupMenu = PopupMenu(requireActivity(), edt_blood_group)
        val menu = popupMenu.menu
        menu.add(0, 1, 0, getString(R.string.title_a_plus))
        menu.add(0, 2, 0, getString(R.string.title_a_minus))
        menu.add(0, 3, 0, getString(R.string.title_b_plus))
        menu.add(0, 4, 0, getString(R.string.title_b_minus))
        menu.add(0, 5, 0, getString(R.string.title_o_plus))
        menu.add(0, 6, 0, getString(R.string.title_o_minus))
        menu.add(0, 7, 0, getString(R.string.title_ab_plus))
        menu.add(0, 8, 0, getString(R.string.title_ab_minus))
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            edt_blood_group?.setText(item.title)
            viewModel.additionalProInfo.value?.bloodGroup = item.title.toString()
            false
        }
        popupMenu.show()

    }

    private fun setViewEnable(isViewEnable: Boolean) {
        edt_height?.isEnabled = isViewEnable
        edt_weight?.isEnabled = isViewEnable
        edt_blood_group?.isEnabled = isViewEnable
        edt_vegetarian?.isEnabled = isViewEnable
        edt_smoke?.isEnabled = isViewEnable
        edt_alcohol?.isEnabled = isViewEnable
        edt_drug?.isEnabled = isViewEnable

    }

    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner,
            Observer {
                if (it) {
                    progress_bar?.visibility = View.VISIBLE
                    sv_main?.visibility = View.INVISIBLE
                }
                else {
                    progress_bar?.visibility = View.INVISIBLE
                    sv_main?.visibility = View.VISIBLE
                }
            })

        viewModel.isViewEnable.observe(viewLifecycleOwner,
            Observer {
//               if (it) btn_edit?.text =getString(R.string.title_edit)
//                else btn_edit?.visibility = getString(R.string.title_save)
            }
        )

        viewModel.resultAdditionInfo.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> viewModel.additionalProInfo.value = it.data?.data
                Status.FAILURE -> showSnackBar(it.errorMsg.toString())
            }
        })

        viewModel.resultUpdateProfileInfo.observe(viewLifecycleOwner, Observer {
            btn_edit.tag = 0
            btn_edit.text = getString(R.string.action_edit)
            setViewEnable(false)
            when (it.status) {
                Status.SUCCESS -> showSnackBar(it.data!!.message, R.color.colorGreen)
                Status.FAILURE -> showSnackBar(it.errorMsg.toString())
            }
        })

        viewModel.additionalProInfo.observe(viewLifecycleOwner, Observer {

            it?.height?.let {
                edt_height?.setText(it.toString())
                edt_height.append(" ")
                edt_height.append("cms")
            }
            it?.weight?.let {
                edt_weight?.setText(it.toString())
                edt_weight.append(" ")
                edt_weight.append("lbs")
            }

            it?.bloodGroup?.let {
                if (it.isNotEmpty()) edt_blood_group?.setText(it)
            }

            it?.isVegetarian?.let {
                if (it) edt_vegetarian.setText(getString(R.string.title_yes))
                else edt_vegetarian.setText(getString(R.string.title_no))
            }

            it?.useSmoke?.let {
                if (it) edt_smoke.setText(getString(R.string.title_yes))
                else edt_smoke.setText(getString(R.string.title_no))
            }

            it?.useAlcohol?.let {
                if (it) edt_alcohol.setText(getString(R.string.title_yes))
                else edt_alcohol.setText(getString(R.string.title_no))
            }

            it?.useDrug?.let {
                if (it) edt_drug.setText(getString(R.string.title_yes))
                else edt_drug.setText(getString(R.string.title_no))
            }
        })


    }


    private fun showAddictionPopupMenu(view: AppCompatEditText) {
        val popupMenu = PopupMenu(requireActivity(), view)
        val menu = popupMenu.menu
        menu.add(0, 1, 0, getString(R.string.title_yes))
        menu.add(0, 2, 0, getString(R.string.title_no))
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            view.setText(item.title)
            when (view.id) {
                R.id.edt_vegetarian -> {
                    if (item.itemId == 1) viewModel.additionalProInfo.value?.isVegetarian = true
                    if (item.itemId == 2) viewModel.additionalProInfo.value?.isVegetarian = false
                }
                R.id.edt_smoke -> {
                    if (item.itemId == 1) viewModel.additionalProInfo.value?.useSmoke = true
                    if (item.itemId == 2) viewModel.additionalProInfo.value?.useSmoke = false
                }
                R.id.edt_alcohol -> {
                    if (item.itemId == 1) viewModel.additionalProInfo.value?.useAlcohol = true
                    if (item.itemId == 2) viewModel.additionalProInfo.value?.useAlcohol = false
                }
                R.id.edt_drug -> {
                    if (item.itemId == 1) viewModel.additionalProInfo.value?.useDrug = true
                    if (item.itemId == 2) viewModel.additionalProInfo.value?.useDrug = false
                }
            }
            false
        }
        popupMenu.show()


    }
}