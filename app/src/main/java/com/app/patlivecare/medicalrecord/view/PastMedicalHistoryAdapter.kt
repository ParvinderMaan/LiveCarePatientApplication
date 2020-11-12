package com.app.patlivecare.medicalrecord.view

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.patlivecare.R
import com.app.patlivecare.medicalrecord.model.PastMedicalHistoryInfo
import kotlinx.android.synthetic.main.list_item_past_medical_history.view.*

class PastMedicalHistoryAdapter : ListAdapter<PastMedicalHistoryInfo,PastMedicalHistoryAdapter.VViewHolder>(ITEM_COMPARATOR) {
    private var itemClickListener: OnItemClickListener? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VViewHolder {
        return VViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_past_medical_history, parent, false))
    }



    interface OnItemClickListener {
        fun onItemMoreClick(model: PastMedicalHistoryInfo, adapterPosition: Int)
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.itemClickListener = mItemClickListener
    }

    class VViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(
            model: PastMedicalHistoryInfo, itemClickListener: OnItemClickListener?) {
            itemView.tv_treatment_name?.text = model.treatmentName
            itemView.tv_doctor_name?.text = model.doctorName
            itemView.tv_date?.text = model.date
            itemView.tv_notes?.text=model.description
            itemView.tv_more_desc.setOnClickListener {
                when( itemView.tv_more_desc.text){
                    "More" ->{
                        itemView.tv_notes.maxLines=Integer.MAX_VALUE
                        itemView.tv_more_desc?.text=this.itemView.context.getString(R.string.action_less)
                    }
                    "Less" ->   {
                        itemView.tv_notes.maxLines=2
                        itemView.tv_more_desc?.text=this.itemView.context.getString(R.string.action_more)
                    }
                }
            }
            itemView.iv_more.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, itemView.iv_more)
                val menu = popupMenu.menu
                menu.add(0, 1, 0, itemView.context.getString(R.string.action_delete))
                popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                    itemClickListener?.onItemMoreClick(model,adapterPosition)
                    false
                }
                popupMenu.show()

            }
            itemView.setOnClickListener {}

          //  val lineCount =  itemView.tv_notes?.lineCount
          //  Log.e("lineCount : ",lineCount.toString())
        }

    }
    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<PastMedicalHistoryInfo>() {
            override fun areItemsTheSame(
                oldItem: PastMedicalHistoryInfo,
                newItem: PastMedicalHistoryInfo
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: PastMedicalHistoryInfo,
                newItem: PastMedicalHistoryInfo
            ): Boolean =
                newItem == oldItem
        }
    }

    override fun onBindViewHolder(holder: VViewHolder, pos: Int) {
        var item = getItem(pos)
        val viewHolder = holder
        item?.let {
            viewHolder.bindView(it,itemClickListener)
        }
        }
}