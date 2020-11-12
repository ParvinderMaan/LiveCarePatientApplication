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
import com.app.patlivecare.medicalrecord.model.AllergyHistoryInfo
import kotlinx.android.synthetic.main.list_item_allergy_history.view.*

class AllergyHistoryAdapter : ListAdapter<AllergyHistoryInfo,AllergyHistoryAdapter.VViewHolder>(ITEM_COMPARATOR) {
    private var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VViewHolder {
        return VViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_allergy_history, parent, false))
    }



    interface OnItemClickListener {
        fun onItemMoreClick(model: AllergyHistoryInfo, adapterPosition: Int)
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.itemClickListener = mItemClickListener
    }




    override fun onBindViewHolder(holder: VViewHolder, pos: Int) {
        val item = getItem(pos)
        val viewHolder = holder
        item?.let {
            viewHolder.bindView(it,itemClickListener)
        }
    }



    class VViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindView(model: AllergyHistoryInfo, itemClickListener:OnItemClickListener?) {
            itemView.tv_allergy_name?.text = model.allergyName
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

            //  val lineCount =  itemView.tv_notes?.lineCount
            //  Log.e("lineCount : ",lineCount.toString())
        }

    }

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<AllergyHistoryInfo>() {
            override fun areItemsTheSame(
                oldItem: AllergyHistoryInfo,
                newItem: AllergyHistoryInfo
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: AllergyHistoryInfo,
                newItem: AllergyHistoryInfo
            ): Boolean =
                newItem == oldItem
        }
    }
}