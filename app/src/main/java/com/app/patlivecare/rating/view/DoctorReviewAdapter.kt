package com.app.patlivecare.rating.view

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.patlivecare.R
import com.app.patlivecare.helper.TimeUtil
import com.app.patlivecare.network.WebUrl
import com.app.patlivecare.rating.model.UserReview
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.list_item_doctor_review.view.*
import kotlinx.android.synthetic.main.list_item_find_doctor.view.*
import kotlinx.android.synthetic.main.list_item_find_doctor.view.tv_doc_name
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class DoctorReviewAdapter : ListAdapter<UserReview, DoctorReviewAdapter.ReviewViewHolder>(ITEM_COMPARATOR) {
    private var itemClickListener: OnItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_doctor_review, parent, false)
        )
    }


    interface OnItemClickListener {
        fun onItemClick(model: UserReview, adapterPosition: Int);
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.itemClickListener = mItemClickListener
    }

    @ExperimentalStdlibApi
    override fun onBindViewHolder(holder: ReviewViewHolder, pos: Int) {
        val item = getItem(pos)
        item.let {
            holder.bindView(item, pos, itemClickListener)
        }

    }

    class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindView(model: UserReview, pos: Int, itemClickListener: OnItemClickListener?) {
            // views
            itemView.setOnClickListener {
                itemClickListener?.onItemClick(model, 0)
            }

            // views
            model.name.let {
                itemView.tv_pat_name?.text =it
            }

            model.reviews.let {
                itemView.tv_review?.text =it
            }

            model.date.let {
                val inFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                val outFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())
                val dateTimeIn = LocalDateTime.parse(it, inFormatter)
                    .atOffset(ZoneOffset.UTC)
                    .atZoneSameInstant(ZoneId.systemDefault())
                itemView.tv_date?.text = dateTimeIn.format(outFormatter)
            }

            if(!model.profilePic.isNullOrEmpty()){
                Glide.with(itemView.context)
                    .load(WebUrl.BASE_FILE + model.profilePic)
                    .centerCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.colorLightestGrey)
                    .into(itemView.civ_pat_pic)
            }else{
                Glide.with(itemView.context)
                    .load(R.drawable.img_avatar)
                    .centerCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(itemView.civ_pat_pic)
            }
        }

    }

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<UserReview>() {
            override fun areItemsTheSame(
                oldItem: UserReview,
                newItem: UserReview
            ): Boolean =
                oldItem.date == newItem.date

            override fun areContentsTheSame(
                oldItem: UserReview,
                newItem: UserReview
            ): Boolean =
                newItem == oldItem
        }
    }
}

