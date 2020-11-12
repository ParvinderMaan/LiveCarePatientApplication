package com.app.patlivecare.notification.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.patlivecare.R
import com.app.patlivecare.blog.model.BlogInfo
import com.app.patlivecare.notification.model.NotificationInfo
import kotlinx.android.synthetic.main.fragment_notification.*


class NotificationFragment : Fragment() {

    companion object {
        fun newInstance() = NotificationFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_notification?.apply {
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            val notificationAdapter = NotificationAdapter()
            adapter = notificationAdapter
            notificationAdapter.setOnItemClickListener(object :
                NotificationAdapter.OnItemClickListener {
                override fun onItemClick(item: NotificationInfo?) {
                }

            })
            notificationAdapter?.addAll(getDummyData())
        }



    }

    private fun getDummyData(): List<NotificationInfo> {
        val items: ArrayList<NotificationInfo> = ArrayList()
        items.add(NotificationInfo(1,"Create your views and add them to the parent ConstraintLayout","","","","1 hour"))
        items.add(NotificationInfo(2,"Actually there are some bugs already filled","","","","5 min"))
        items.add(NotificationInfo(3,"Now we just need to constrain a view to the start edge of the parent","","","",""))
        items.add(NotificationInfo(4,"ConstraintLayout is available as a support library that you can","","","",""))
        items.add(NotificationInfo(5,"Actually there are some bugs already filled ","","","",""))
        return items
    }


}