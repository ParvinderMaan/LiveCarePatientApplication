package com.app.patlivecare.prescription.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.patlivecare.R
import com.app.patlivecare.prescription.model.MedicalTestInfo

class MedicalTestAdapter () : RecyclerView.Adapter<MedicalTestAdapter.DoctorViewHolder>() {
    private var mItemClickListener: OnItemClickListener? = null
    val items: ArrayList<MedicalTestInfo>

    init {
        this.items = ArrayList()
    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        return DoctorViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_medical_test, parent, false)
        )
    }



    interface OnItemClickListener {
        fun onItemClick(model: MedicalTestInfo, adapterPosition: Int);
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.mItemClickListener = mItemClickListener
    }


    fun add(item: MedicalTestInfo) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun addAll(items: List<MedicalTestInfo>) {
        for (item in items) {
            add(item)
        }
    }


    // Update ALL VIEW holder
    override fun onBindViewHolder(holder: DoctorViewHolder, pos: Int) {
//        val item = items.get(pos)
//        val viewHolder = holder
//        item.let {
//            viewHolder.bindView(item)
//
//        }
//        viewHolder.itemView.setOnClickListener{}
//        // listener
//        viewHolder.itemView.tv_view_details?.setOnClickListener {
//
//            mItemClickListener?.onItemClick(item,viewHolder.adapterPosition)
//
//
//        }

    }
    class DoctorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(model: MedicalTestInfo) {
            // views
//            itemView.tv_presc_title?.text = (model.title)
//            itemView.tv_presc_desc?.text = (model.desc)

        }


    }
}