package com.app.patlivecare.prescription.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.patlivecare.R
import com.app.patlivecare.prescription.model.PrescriptionInfo
import kotlinx.android.synthetic.main.list_item_prescription.view.*

class PrescriptionAdapter() : RecyclerView.Adapter<PrescriptionAdapter.DoctorViewHolder>() {
    private var mItemClickListener: OnItemClickListener? = null
    val items: ArrayList<PrescriptionInfo>

    init {
        this.items = ArrayList()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        return DoctorViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_prescription, parent, false)
        )
    }



    interface OnItemClickListener {
        fun onItemClick(model: PrescriptionInfo, adapterPosition: Int);
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.mItemClickListener = mItemClickListener
    }


    fun add(item: PrescriptionInfo) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun addAll(items: List<PrescriptionInfo>) {
        for (item in items) {
            add(item)
        }
    }


    // Update ALL VIEW holder
    override fun onBindViewHolder(holder: DoctorViewHolder, pos: Int) {
            val item = items.get(pos)
            val viewHolder = holder
        item.let {
            viewHolder.bindView(item)

        }
        viewHolder.itemView.setOnClickListener{}
        // listener
     viewHolder.itemView.tv_view_details?.setOnClickListener {

            mItemClickListener?.onItemClick(item,viewHolder.adapterPosition)


        }

    }
    class DoctorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(model: PrescriptionInfo) {
            // views
           itemView.tv_presc_title?.text = (model.title)
          itemView.tv_presc_desc?.text = (model.desc)

        }


    }
}