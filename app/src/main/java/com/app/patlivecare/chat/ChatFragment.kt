package com.app.patlivecare.chat

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.patlivecare.R
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import kotlinx.android.synthetic.main.fragment_chat.*


class ChatFragment : Fragment() {


    private var mFragmentListener: HomeFragmentSelectedListener? = null

    companion object {

        fun newInstance() = ChatFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeFragmentSelectedListener) mFragmentListener = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // rv_chat


        ibtn_close.setOnClickListener {
            mFragmentListener?.popTopMostFragment()
        }
    }

}