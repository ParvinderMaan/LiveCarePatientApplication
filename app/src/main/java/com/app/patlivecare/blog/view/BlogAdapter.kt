package com.app.patlivecare.blog.view

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.patlivecare.R
import com.app.patlivecare.blog.model.BlogInfo
import com.app.patlivecare.helper.TimeUtil
import com.app.patlivecare.network.WebUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.list_item_blog.view.*
import kotlinx.android.synthetic.main.list_item_blog.view.tv_doc_name
import kotlinx.android.synthetic.main.list_item_top_doctor_i.view.*
import java.util.*


class BlogAdapter() : PagedListAdapter<BlogInfo,BlogAdapter.MiViewHolder>(ITEM_COMPARATOR) {
    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(item: BlogInfo)
    }

    public fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.itemClickListener = mItemClickListener

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiViewHolder {
        return MiViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_blog, parent, false))
    }

    override fun onBindViewHolder(holder: MiViewHolder, pos: Int) {
        val item = getItem(pos)
        val viewHolder = holder
        item?.let { viewHolder.bindView(it,itemClickListener) }
    }

    class MiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(model: BlogInfo, itemClickListener: OnItemClickListener?) {
            val bgColor = when (adapterPosition % 5) {
                0 -> Color.parseColor("#e5eef5")
                1 -> Color.parseColor("#eff8ff")
                2 -> Color.parseColor("#ffeffb")
                3 -> Color.parseColor("#dfebcc")
                4 -> Color.parseColor("#fff8ef")
                else -> R.color.colorLightestGrey
            }
            itemView.iv_blog?.setBackgroundColor(bgColor)

            itemView.tv_blog_title?.text = model.title
            itemView.tv_doc_name?.text = model.doctorName
            val createdOn = TimeUtil.utcToLocal(model.createdDate)
            itemView.tv_created_on?.text = createdOn
            Glide.with(itemView.context)
                    .load(WebUrl.BASE_FILE+model.imgUrl)
                    .centerCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.colorLightestGrey)
                    .into(itemView.iv_blog)

            Glide.with(itemView.context)
                .load(WebUrl.BASE_FILE+model.doctorProfilePic)
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.color.colorLightestGrey)
                .into(itemView.iv_doctor)

            itemView.setOnClickListener {
                itemClickListener?.onItemClick(model)
            }


        }
    }

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<BlogInfo>() {
            override fun areItemsTheSame(oldItem: BlogInfo, newItem: BlogInfo): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: BlogInfo, newItem: BlogInfo): Boolean = newItem == oldItem
        }
    }




}

