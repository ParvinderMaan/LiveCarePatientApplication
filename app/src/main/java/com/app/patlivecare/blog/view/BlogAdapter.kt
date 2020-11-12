package com.app.patlivecare.blog.view

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
import java.util.*


class BlogAdapter(var timeNow: Date) : PagedListAdapter<BlogInfo,BlogAdapter.MiViewHolder>(ITEM_COMPARATOR) {
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
        fun bindView(
            model: BlogInfo, itemClickListener: OnItemClickListener?) {
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

