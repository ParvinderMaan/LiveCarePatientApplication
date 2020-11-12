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
import com.app.patlivecare.medicalrecord.model.AttachmentHistoryInfo
import kotlinx.android.synthetic.main.list_item_attachment_and_report.view.*

class AttachmentAndReportAdapter : ListAdapter<AttachmentHistoryInfo,AttachmentAndReportAdapter.VViewHolder>(ITEM_COMPARATOR) {
    private var itemClickListener: OnItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VViewHolder {
        return VViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_attachment_and_report, parent, false))
    }

    override fun onBindViewHolder(holder: AttachmentAndReportAdapter.VViewHolder, pos: Int) {
        var item = getItem(pos)
        val viewHolder = holder
        item?.let {
            viewHolder.bindView(it,itemClickListener)
        }
    }

    interface OnItemClickListener {
        fun onItemMoreClick(model: AttachmentHistoryInfo, adapterPosition: Int)
        fun onItemViewReportClick(model: AttachmentHistoryInfo)
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.itemClickListener = mItemClickListener
    }



    class VViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindView(model: AttachmentHistoryInfo, itemClickListener: OnItemClickListener?) {
            itemView.tv_name?.text = model.reportName
            itemView.tv_date?.text = model.date
            itemView.tv_desc?.text = model.description
            itemView.tv_more_desc.setOnClickListener {
                when( itemView.tv_more_desc.text){
                    "More" ->{
                        itemView.tv_desc.maxLines=Integer.MAX_VALUE
                        itemView.tv_more_desc?.text=this.itemView.context.getString(R.string.action_less)
                    }
                    "Less" ->   {
                        itemView.tv_desc.maxLines=2
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

            itemView.tv_view_report.setOnClickListener {
                itemClickListener?.onItemViewReportClick(model)
            }
        }



    }

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<AttachmentHistoryInfo>() {
            override fun areItemsTheSame(
                oldItem: AttachmentHistoryInfo,
                newItem: AttachmentHistoryInfo
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: AttachmentHistoryInfo,
                newItem: AttachmentHistoryInfo
            ): Boolean =
                newItem == oldItem
        }
    }
}