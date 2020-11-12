package com.app.patlivecare.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.patlivecare.R
import com.app.patlivecare.CountryCodeModel
import kotlinx.android.synthetic.main.dialog_country_code.*

class CountryCodeDialogFragment : DialogFragment() {
    private var dialogListener: CountryCodeDialogFragmentListener? = null

    companion object {
        fun newInstance(): CountryCodeDialogFragment {
            return CountryCodeDialogFragment()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Light_NoTitleBar)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_country_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        isCancelable=false
//        btn_submit_quiz?.setOnClickListener {
//            dialogListener?.onSubmitClick()
//        }
        initRecyclerView()


    }

    private fun initRecyclerView() {
        rv_country?.apply {
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            val countryCodeAdapter = CountryCodeAdapter()
            adapter = CountryCodeAdapter()
            countryCodeAdapter.setOnItemClickListener(object : CountryCodeAdapter.OnItemClickListener {
                override fun onItemClick(model: CountryCodeModel, adapterPosition: Int) {
                    dialogListener?.onSubmitClick()

                }
            })
            rv_country.setHasFixedSize(true)
        }

    }

    interface CountryCodeDialogFragmentListener {
        fun onSubmitClick()
    }

    fun setOnCountryCodeDialogFragmentListener(listener: CountryCodeDialogFragmentListener) {
        dialogListener = listener
    }

    class CountryCodeAdapter : RecyclerView.Adapter<CountryCodeAdapter.CountryCodeViewHolder> {
        private var mItemClickListener: OnItemClickListener? = null
        val items: ArrayList<CountryCodeModel>

        constructor() : super() {
            this.items = ArrayList()
        }

        override fun getItemCount(): Int {
            return 30
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryCodeViewHolder {
            return CountryCodeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_country_code,
                    parent, false))
        }



        // Update ALL VIEW holder
        override fun onBindViewHolder(holder: CountryCodeViewHolder, pos: Int) {
            //  Log.e("onBindViewHolder", ".............normal............."+pos)
           // var item = items.get(pos)
            val viewHolder = holder
//            item.let {
//                viewHolder.bindView(it, itemCount)
//            }

            // listener
        }

        interface OnItemClickListener {
            fun onItemClick(model: CountryCodeModel, adapterPosition: Int);
        }

        fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
            this.mItemClickListener = mItemClickListener;
        }


        fun add(item: CountryCodeModel) {
            items.add(item)
            notifyItemInserted(items.size - 1)
        }

        fun addAll(items: List<CountryCodeModel>) {
            for (item in items) {
                add(item)
            }
        }
        class CountryCodeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            @Suppress("DEPRECATION")
            fun bindView(model: CountryCodeModel, totCount: Int) {
                // views

             //   var quesTitle = itemView.context.resources.getString(R.string.title_question)
             //   itemView.tv_ques_no?.text = (quesTitle).plus(" ").plus(adapterPosition+1)




            }


        }
    }

}