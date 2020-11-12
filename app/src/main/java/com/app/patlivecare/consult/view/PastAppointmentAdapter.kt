package com.app.patlivecare.consult.view

import android.graphics.Color
import android.view.*
import android.widget.ImageButton
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.patlivecare.R
import com.app.patlivecare.annotation.AppointmentStatus
import com.app.patlivecare.consult.model.AppointmentInfo
import com.app.patlivecare.network.WebUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.list_item_past_consult.view.*
import kotlinx.android.synthetic.main.list_item_past_consult.view.civ_doc
import kotlinx.android.synthetic.main.list_item_past_consult.view.ibtn_more
import kotlinx.android.synthetic.main.list_item_past_consult.view.tv_date_of_month
import kotlinx.android.synthetic.main.list_item_past_consult.view.tv_doc_name
import kotlinx.android.synthetic.main.list_item_past_consult.view.tv_speciality
import kotlinx.android.synthetic.main.list_item_past_consult.view.tv_time_slot
import kotlinx.android.synthetic.main.list_item_upcoming_consult.view.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.util.*

class PastAppointmentAdapter : PagedListAdapter<AppointmentInfo,PastAppointmentAdapter.MiViewHolder>(ITEM_COMPARATOR) {
    private var mItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(item: AppointmentInfo?)
        fun onItemMoreClick(view: ImageButton, item: AppointmentInfo)
    }

    public fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.mItemClickListener = mItemClickListener;

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiViewHolder {
        return MiViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_past_consult,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MiViewHolder, pos: Int) {
        holder.itemView.setOnClickListener {}
        val item = getItem(pos)
        val viewHolder = holder
        item?.let {itt ->
            viewHolder.bindView(itt,viewHolder.adapterPosition)
            holder.itemView.ibtn_more?.setOnClickListener {
                mItemClickListener?.onItemMoreClick(holder.itemView.ibtn_more,itt)
       }

        }
        holder.itemView?.setOnClickListener {
            mItemClickListener?.onItemClick(item)
        }


    }

    class MiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
            itemView.tv_speciality?.text = model.doctorSpecialities.get(0).name

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

            model.slotFrom?.let {
                val inFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                val outFormat = DateTimeFormatter.ofPattern("MMM")
                val nameOfMonth = outFormat.format(inFormat.parse(it))
                val dayOfMonth = it.split("-")[0]
                itemView.tv_date_of_month?.text = dayOfMonth.plus(" ").plus(nameOfMonth)
            }
//            val inFormatter= DateTimeFormatter.ofPattern("HH:mm:ss")
//            val inTimeFrom: LocalTime = LocalTime.parse(model.slotFrom,inFormatter)
//            val outTimeFrom = DateTimeFormatter.ofPattern("HH:mm").format(inTimeFrom)
//
//            val inTimeTo: LocalTime = LocalTime.parse(model.slotTo,inFormatter)
//            val outTimeTo = DateTimeFormatter.ofPattern("HH:mm").format(inTimeTo)



            if(model.slotFrom !=null && model.slotTo!=null){
                val inFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                val outFormat = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
                val slotFrom = outFormat.format(inFormat.parse(model.slotFrom))
                val slotTo = outFormat.format(inFormat.parse(model.slotTo))
                itemView.tv_time_slot?.text=slotFrom.plus("-").plus(slotTo)
            }

            when(model.appointmentStatus){
                AppointmentStatus.CONFIRMED -> {
                    itemView.appointment_status_.visibility = View.VISIBLE
                    itemView.appointment_status_.visibility = View.VISIBLE
                    itemView.appointment_status.visibility = View.VISIBLE
                    if(model.appointmentStatus==2) itemView.appointment_status_.text="Completed"
                }
                AppointmentStatus.PENDING -> {
                    itemView.appointment_status_.visibility = View.INVISIBLE
                    itemView.appointment_status.visibility = View.INVISIBLE
                }
            }
        }


    }

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<AppointmentInfo>() {
            override fun areItemsTheSame(oldItem: AppointmentInfo, newItem: AppointmentInfo): Boolean = oldItem.appointmentId == newItem.appointmentId
            override fun areContentsTheSame(oldItem: AppointmentInfo, newItem: AppointmentInfo): Boolean = newItem == oldItem
        }
    }
}