package com.app.patlivecare.consult.view

import android.graphics.Color
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.patlivecare.R
import com.app.patlivecare.annotation.AppointmentStatus
import com.app.patlivecare.consult.model.AppointmentInfo
import com.app.patlivecare.helper.TimeUtil
import com.app.patlivecare.network.WebUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.list_item_upcoming_consult.view.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class UpcomingAppointmentAdapter() : ListAdapter<AppointmentInfo, UpcomingAppointmentAdapter.MiViewHolder>(ITEM_COMPARATOR) {
    private var mItemClickListener: OnItemClickListener? = null


    interface OnItemClickListener {
        fun onItemClick(item: AppointmentInfo?)
        fun onItemMoreClick(item: AppointmentInfo?, adapterPosition: Int, view: ImageButton)
    }

    public fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.mItemClickListener = mItemClickListener

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiViewHolder {
        return MiViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_upcoming_consult,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MiViewHolder, pos: Int) {
    //   val createdOn = TimeUtil.utcToLocal(item.createdOn)

        val item = getItem(pos)
        val viewHolder = holder
        item?.let {
            viewHolder.bindView(it, viewHolder.adapterPosition)
        }

        holder.itemView.setOnClickListener {
            mItemClickListener?.onItemClick(item)
        }

        viewHolder.itemView.ibtn_more?.setOnClickListener {
            mItemClickListener?.onItemMoreClick(item, viewHolder.adapterPosition, viewHolder.itemView.ibtn_more)
        }
    }


    class MiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
         var timer:CountDownTimer?=null
        fun bindView(model: AppointmentInfo, pos: Int) {

            itemView.context.getString(R.string.title_dr)
                .plus(" ")
                .plus(model.firstName)
                .plus(" ")
                .plus(model.lastName)?.let {
                    itemView.tv_doc_name?.text =it
                }

            model.doctorSpecialities.get(0).name?.let {
                itemView.tv_speciality?.text =it
            }

            itemView.tv_speciality?.text = model.doctorSpecialities[0].name

            if(!model.profilePic.isNullOrEmpty()){
                Glide.with(itemView.context)
                    .load(WebUrl.BASE_FILE + model.profilePic)
                    .centerCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(itemView.civ_doc)
            }else{
                val bgColor = when (pos % 5) {
                    0 -> Color.parseColor("#e5eef5")
                    1 -> Color.parseColor("#eff8ff")
                    2 -> Color.parseColor("#ffeffb")
                    3 -> Color.parseColor("#dfebcc")
                    4 -> Color.parseColor("#fff8ef")
                    else -> R.color.colorLightestGrey
                }
                itemView.civ_doc?.setBackgroundColor(bgColor)
            }

            if(model.slotFrom !=null && model.slotTo!=null){
                val inFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                val outFormat = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
                val slotFrom = outFormat.format(inFormat.parse(model.slotFrom))
                val slotTo = outFormat.format(inFormat.parse(model.slotTo))
                itemView.tv_time_slot?.text=slotFrom.plus("-").plus(slotTo)
            }

            when(model.appointmentStatus){
                AppointmentStatus.CONFIRMED -> {
                    itemView.tv_payment_status_.visibility = View.VISIBLE
                    itemView.tv_payment_status.visibility = View.VISIBLE
                    itemView.tv_payment_status.visibility = View.VISIBLE
                    if (model.paymentStatus == 2) itemView.tv_payment_status.text = "Completed"

                    model.slotFrom?.let {
                        val inFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                        val outFormat = DateTimeFormatter.ofPattern("MMM")
                        val nameOfMonth = outFormat.format(inFormat.parse(it))
                        val dayOfMonth = it.split("-")[0]
                        itemView.tv_date_of_month?.text = dayOfMonth.plus(" ").plus(nameOfMonth)
                    }

                    if (model.remainingTime == -1L) {
                        itemView.textView?.text ="Call in Progress.."
                    }else{
                        //timer
                        if (timer == null) {
                            //  Log.e("new object", (dateTime.second *1000).toString())
                            timer = object : CountDownTimer(model.remainingTime, 1000) {
                                override fun onTick(millisUntilFinished: Long) {
                                    itemView.textView?.text = TimeUtil.getTimeSpan(millisUntilFinished)
                                    model.remainingTime=millisUntilFinished
                                }
                                override fun onFinish() {
                                    timer == null
                                    //   Log.e("new object",pos.toString()+" dead")
                                }
                            }.start()
                        }
                    }


                }
                AppointmentStatus.PENDING -> {
                    itemView.tv_payment_status_.visibility = View.INVISIBLE
                    itemView.tv_payment_status.visibility = View.INVISIBLE

                    model.slotFrom?.let {
                        val inFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                        val outFormat = DateTimeFormatter.ofPattern("MMM")
                        val nameOfMonth = outFormat.format(inFormat.parse(it))
                        val dayOfMonth = it.split("-")[0]
                        itemView.tv_date_of_month?.text = dayOfMonth.plus(" ").plus(nameOfMonth)
                    }

                }
            }
        }
    }

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<AppointmentInfo>() {
            override fun areItemsTheSame(oldItem: AppointmentInfo, newItem: AppointmentInfo): Boolean = oldItem.appointmentId == newItem.appointmentId
            override fun areContentsTheSame(oldItem: AppointmentInfo, newItem: AppointmentInfo): Boolean = newItem == oldItem
        }

        private lateinit var timeNow:ZonedDateTime
    }
}

