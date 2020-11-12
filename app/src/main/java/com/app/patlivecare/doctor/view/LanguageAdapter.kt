package com.app.patlivecare.doctor.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.patlivecare.R
import com.app.patlivecare.doctor.model.DoctorDetailResponse
import com.app.patlivecare.doctor.model.DoctorInfo
import kotlinx.android.synthetic.main.list_item_language.view.*

class LanguageAdapter : ListAdapter<DoctorDetailResponse.Language,LanguageAdapter.SpecialityViewHolder>(ITEM_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialityViewHolder {
        return SpecialityViewHolder(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_language, parent, false))
    }



    override fun onBindViewHolder(holder: SpecialityViewHolder, pos: Int) {
        val item = getItem(pos)
        item.let {
            holder.bindView(item)
        }
    }


    class SpecialityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(model: DoctorDetailResponse.Language) {
            // views
            itemView.tv_language?.text = (model.name)
            // itemView.iv_category
        }


    }

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<DoctorDetailResponse.Language>() {
            override fun areItemsTheSame(
                oldItem: DoctorDetailResponse.Language,
                newItem: DoctorDetailResponse.Language
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: DoctorDetailResponse.Language,
                newItem: DoctorDetailResponse.Language
            ): Boolean =
                newItem == oldItem
        }
    }
}
