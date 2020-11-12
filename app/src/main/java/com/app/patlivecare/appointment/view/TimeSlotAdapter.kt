package com.app.patlivecare.appointment.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.patlivecare.R
import com.app.patlivecare.appointment.model.TimeSlotInfo
import com.app.patlivecare.doctor.model.DoctorInfo
import kotlinx.android.synthetic.main.list_item_time_slot.view.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*


class TimeSlotAdapter : ListAdapter<TimeSlotInfo,TimeSlotAdapter.VViewHolder>(ITEM_COMPARATOR) {
    private var itemClickListener: OnItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VViewHolder {
        return VViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_time_slot, parent, false))
    }

    interface OnItemClickListener {
        fun onItemClick(model: TimeSlotInfo, adapterPosition: Int);
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.itemClickListener = mItemClickListener
    }


//    fun add(item: TimeSlotInfo) {
//        items.add(item)
//        notifyItemInserted(items.size - 1)
//    }
//
//    fun addAll(items: List<TimeSlotInfo>) {
//        for (item in items) {
//            add(item)
//        }
//    }


    // Update ALL VIEW holder
    override fun onBindViewHolder(holder: VViewHolder, pos: Int) {
        val item = getItem(pos)
        val viewHolder = holder
        item.let {
            viewHolder.bindView(item,itemClickListener)

        }

        // listener
//     viewHolder.itemView.tv_ans_one?.setOnClickListener {
//            if (item.ansOptSelected != 1 && item.ansOptSelected != 2 && item.ansOptSelected != 3 && item.ansOptSelected != 4) {
//                item.ansOptSelected = 1
//                notifyItemChanged(viewHolder.adapterPosition,1)
//                mItemClickListener?.onItemClick(item,viewHolder.adapterPosition)
//            }
//
//        }

    }
    class VViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(model: TimeSlotInfo, itemClickListener: OnItemClickListener?) {
            // views
          //  itemView.tv_name?.text = model.timeSlotId.toString()


            if(model.isSlotAvailable)  itemView.tv_name.isSelected=false
            else itemView.tv_name.isSelected=true

            val inFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss a", Locale.getDefault()) // 01-09-2020 ---> 1-9-2020
            val outFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault()) // 01-09-2020 ---> 1-9-2020
            val dateTime = LocalDateTime.parse(model.slotFrom, inFormatter)
            val formattedDate: String =dateTime.format(outFormatter)
          //  val formattedDate: String =dateTime.format(inFormatter)
            itemView.tv_name?.text =formattedDate
            itemView.setOnClickListener {
                itemClickListener?.onItemClick(model,0)
            }
        }


    }

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<TimeSlotInfo>() {
            override fun areItemsTheSame(
                oldItem: TimeSlotInfo,
                newItem: TimeSlotInfo
            ): Boolean =
                oldItem.timeSlotId == newItem.timeSlotId

            override fun areContentsTheSame(
                oldItem: TimeSlotInfo,
                newItem: TimeSlotInfo
            ): Boolean =
                newItem == oldItem
        }
    }
}