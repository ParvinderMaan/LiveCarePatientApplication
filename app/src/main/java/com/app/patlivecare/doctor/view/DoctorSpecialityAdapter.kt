package com.app.patlivecare.doctor.view

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.patlivecare.R
import com.app.patlivecare.doctor.model.SpecialityInfo
import com.app.patlivecare.network.WebUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.list_item_speciality_i.view.*

class DoctorSpecialityAdapter(@LayoutRes val resource: Int) :
    ListAdapter<SpecialityInfo, DoctorSpecialityAdapter.SpecialityViewHolder>(ITEM_COMPARATOR) {
    private var itemClickListener: OnItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialityViewHolder {
        return SpecialityViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(resource, parent, false)
        )
    }


    interface OnItemClickListener {
        fun onItemClick(model: SpecialityInfo, adapterPosition: Int);
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.itemClickListener = mItemClickListener
    }


    override fun onBindViewHolder(holder: SpecialityViewHolder, pos: Int) {
        val item = getItem(pos)
        item.let {
            holder.bindView(item, pos, itemClickListener)
        }

    }

    class SpecialityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(model: SpecialityInfo, pos: Int, itemClickListener: OnItemClickListener?) {
            // views
            itemView.tv_category_title?.text = (model.name)
            itemView.setOnClickListener {
                itemClickListener?.onItemClick(model, 0)
            }

//            if(model.imgUrl==null) model.imgUrl=""
//            Glide.with(itemView.context)
//                 .load(WebUrl.BASE_FILE+model.imgUrl)
//                 .centerCrop()
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(R.color.colorWhite)
//                .into(itemView.iv_category_)

            when (pos % 5) {
                0 -> itemView.iv_category.setBackgroundColor(Color.parseColor("#e5eef5"))
                1 -> itemView.iv_category.setBackgroundColor(Color.parseColor("#eff8ff"))
                2 -> itemView.iv_category.setBackgroundColor(Color.parseColor("#ffeffb"))
                3 -> itemView.iv_category.setBackgroundColor(Color.parseColor("#dfebcc"))
                4 -> itemView.iv_category.setBackgroundColor(Color.parseColor("#fff8ef"))
            }
        }


    }

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<SpecialityInfo>() {
            override fun areItemsTheSame(
                oldItem: SpecialityInfo,
                newItem: SpecialityInfo
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: SpecialityInfo,
                newItem: SpecialityInfo
            ): Boolean =
                newItem == oldItem
        }
    }
}

