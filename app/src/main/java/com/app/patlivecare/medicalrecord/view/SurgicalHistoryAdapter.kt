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
import com.app.patlivecare.medicalrecord.model.SurgicalHistoryInfo
import kotlinx.android.synthetic.main.list_item_surgical_history.view.*
import kotlinx.android.synthetic.main.list_item_surgical_history.view.tv_date
import kotlinx.android.synthetic.main.list_item_surgical_history.view.tv_doctor_name
import kotlinx.android.synthetic.main.list_item_surgical_history.view.tv_notes
import kotlinx.android.synthetic.main.list_item_surgical_history.view.tv_treatment_name

class SurgicalHistoryAdapter: ListAdapter<SurgicalHistoryInfo,SurgicalHistoryAdapter.VViewHolder>(ITEM_COMPARATOR) {
    private var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VViewHolder {
        return VViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_surgical_history, parent, false))
    }



    interface OnItemClickListener {
        fun onItemMoreClick(model: SurgicalHistoryInfo, adapterPosition: Int)
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.itemClickListener = mItemClickListener
    }


    override fun onBindViewHolder(holder: SurgicalHistoryAdapter.VViewHolder, pos: Int) {
        var item = getItem(pos)
        val viewHolder = holder
        item?.let {
            viewHolder.bindView(it,itemClickListener)
        }
    }



    class VViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(
            model: SurgicalHistoryInfo, itemClickListener: OnItemClickListener?) {
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
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<SurgicalHistoryInfo>() {
            override fun areItemsTheSame(
                oldItem: SurgicalHistoryInfo,
                newItem: SurgicalHistoryInfo
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: SurgicalHistoryInfo,
                newItem: SurgicalHistoryInfo
            ): Boolean =
                newItem == oldItem
        }
    }
}