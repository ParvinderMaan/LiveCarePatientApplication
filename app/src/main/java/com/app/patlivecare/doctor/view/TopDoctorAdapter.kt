package com.app.patlivecare.doctor.view

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.patlivecare.R
import com.app.patlivecare.doctor.model.DoctorInfo
import com.app.patlivecare.network.WebUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.list_item_find_doctor.view.tv_doc_name
import kotlinx.android.synthetic.main.list_item_find_doctor.view.tv_speciality
import kotlinx.android.synthetic.main.list_item_top_doctor_i.view.*
import java.util.*

class TopDoctorAdapter() : ListAdapter<DoctorInfo, TopDoctorAdapter.VViewHolder>(ITEM_COMPARATOR) {
    private var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VViewHolder {
        return VViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_top_doctor_i, parent, false))
    }



    interface OnItemClickListener {
        fun onItemClick(model: DoctorInfo, adapterPosition: Int)
        fun onItemBookNowClick(model: DoctorInfo, adapterPosition: Int)
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.itemClickListener = mItemClickListener
    }


//    fun add(item: DoctorInfo) {
//        items.add(item)
//        notifyItemInserted(items.size - 1)
//    }
//
//    fun addAll(items: List<DoctorInfo>) {
//        for (item in items) {
//            add(item)
//        }
//    }


    // Update ALL VIEW holder
    @ExperimentalStdlibApi
    override fun onBindViewHolder(holder: VViewHolder, pos: Int) {
        val item = getItem(pos)
        val viewHolder = holder
        item?.let {
            viewHolder.bindView(item,pos,itemClickListener)

        }


    }
    class VViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @ExperimentalStdlibApi
        fun bindView(model: DoctorInfo, pos: Int, itemClickListener: OnItemClickListener?) {
            // views
            val fullName = model.name.split(" ").toMutableList()
            var output = ""
            for(name in fullName){ output += name.capitalize(Locale.getDefault()) +" " }
            itemView.tv_doc_name?.text = (output)

            if(model.doctorSpecialities.isNotEmpty()){
                itemView.tv_speciality?.text = model.doctorSpecialities[0].name
            }

            val bgColor = when (pos % 5) {
                0 -> Color.parseColor("#e5eef5")
                1 -> Color.parseColor("#eff8ff")
                2 -> Color.parseColor("#ffeffb")
                3 -> Color.parseColor("#dfebcc")
                4 -> Color.parseColor("#fff8ef")
                else -> R.color.colorLightestGrey
            }
            itemView.iv_doc_pic.setBackgroundColor(bgColor)

            model.profilePic?.let {
                Glide.with(itemView.context)
                    .load(WebUrl.BASE_FILE + model.profilePic)
                    .centerCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(bgColor)
                    .into(itemView.iv_doc_pic)
            }


            when(model.rating){
                1 -> {
                    itemView.iv_star_one.setImageResource(R.drawable.ic_star_on_yellow)
                    itemView.iv_star_two.setImageResource(R.drawable.ic_star_off_yellow)
                    itemView.iv_star_three.setImageResource(R.drawable.ic_star_off_yellow)
                    itemView.iv_star_four.setImageResource(R.drawable.ic_star_off_yellow)
                    itemView.iv_star_five.setImageResource(R.drawable.ic_star_off_yellow)
                }
                2 ->{
                    itemView.iv_star_one.setImageResource(R.drawable.ic_star_on_yellow)
                    itemView.iv_star_two.setImageResource(R.drawable.ic_star_on_yellow)
                    itemView.iv_star_three.setImageResource(R.drawable.ic_star_off_yellow)
                    itemView.iv_star_four.setImageResource(R.drawable.ic_star_off_yellow)
                    itemView.iv_star_five.setImageResource(R.drawable.ic_star_off_yellow)
                }
                3 ->{
                    itemView.iv_star_one.setImageResource(R.drawable.ic_star_on_yellow)
                    itemView.iv_star_two.setImageResource(R.drawable.ic_star_on_yellow)
                    itemView.iv_star_three.setImageResource(R.drawable.ic_star_on_yellow)
                    itemView.iv_star_four.setImageResource(R.drawable.ic_star_off_yellow)
                    itemView.iv_star_five.setImageResource(R.drawable.ic_star_off_yellow)
                }
                4 ->{
                    itemView.iv_star_one.setImageResource(R.drawable.ic_star_on_yellow)
                    itemView.iv_star_two.setImageResource(R.drawable.ic_star_on_yellow)
                    itemView.iv_star_three.setImageResource(R.drawable.ic_star_on_yellow)
                    itemView.iv_star_four.setImageResource(R.drawable.ic_star_on_yellow)
                    itemView.iv_star_five.setImageResource(R.drawable.ic_star_off_yellow)
                }
                5 ->{
                    itemView.iv_star_one.setImageResource(R.drawable.ic_star_on_yellow)
                    itemView.iv_star_two.setImageResource(R.drawable.ic_star_on_yellow)
                    itemView.iv_star_three.setImageResource(R.drawable.ic_star_on_yellow)
                    itemView.iv_star_four.setImageResource(R.drawable.ic_star_on_yellow)
                    itemView.iv_star_five.setImageResource(R.drawable.ic_star_on_yellow)
                }
            }

            itemView.setOnClickListener {
                itemClickListener?.onItemClick(model, 0)
            }
            itemView.btn_book_now?.setOnClickListener {
                itemClickListener?.onItemBookNowClick(model, 0)
            }



        }

    }
        companion object {
            private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<DoctorInfo>() {
                override fun areItemsTheSame(
                    oldItem: DoctorInfo,
                    newItem: DoctorInfo
                ): Boolean =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: DoctorInfo,
                    newItem: DoctorInfo
                ): Boolean =
                    newItem == oldItem
            }
        }

}

