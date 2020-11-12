package com.app.patlivecare.notification.view

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.patlivecare.R
import com.app.patlivecare.notification.model.NotificationInfo
import kotlinx.android.synthetic.main.list_item_notification.view.*


class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.MiViewHolder> {
    private var mItemClickListener: OnItemClickListener? = null
    val items: ArrayList<NotificationInfo>

    constructor() : super() {
        this.items = ArrayList();
    }

    interface OnItemClickListener {
        fun onItemClick(item: NotificationInfo?)
    }

    public fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.mItemClickListener = mItemClickListener;

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiViewHolder {
        return MiViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_notification,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MiViewHolder, pos: Int) {
//        var item = getItem(pos)

//        val viewHolder = holder
//        item?.let { viewHolder.bindView(it)
//         //   val createdOn = TimeUtil.utcToLocal(item.createdOn)
//         //   val date1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(createdOn)
//
//        }
//        holder.itemView.setOnClickListener {
//            mItemClickListener?.onItemClick(item)
//        }
        var item = items.get(pos)
        val viewHolder = holder
        item.let {
            viewHolder.bindView(it,viewHolder.adapterPosition)
        }



    }

    class MiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(model: NotificationInfo,pos:Int) {
            itemView.tv_title?.text = model.title
            itemView.tv_created_on?.text = model.createdOn
//            itemView.tv_created_on?.text = model.createdOn.split(" ")[0]
//           // tv_chapter_status
//            itemView.tv_chapter_view_count?.text=model.viewCount.plus(" ").plus(this.itemView.context.getString(R.string.title_views))
//
            if(pos%2==0){
                itemView.cl_parent.setBackgroundColor(Color.parseColor("#f3f3f3"))
            }else{
                itemView.cl_parent.setBackgroundColor(Color.parseColor("#ffffff"))
            }

        }


    }

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<NotificationInfo>() {
            override fun areItemsTheSame(oldItem: NotificationInfo, newItem: NotificationInfo): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: NotificationInfo, newItem: NotificationInfo): Boolean = newItem == oldItem
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }


    fun add(item: NotificationInfo) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun addAll(items: List<NotificationInfo>) {
        for (item in items) {
            add(item)
        }
    }


}

