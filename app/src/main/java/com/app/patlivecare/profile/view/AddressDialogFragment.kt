package com.app.patlivecare.profile.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.patlivecare.R
import com.app.patlivecare.profile.model.CountryInfoResponse
import com.app.patlivecare.profile.model.HeightInfo
import com.app.patlivecare.profile.model.StateInfoResponse
import com.app.patlivecare.profile.model.WeightInfo
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_unit_convertor.*
import kotlinx.android.synthetic.main.list_item_address.view.*
import java.util.ArrayList


class AddressDialogFragment : BottomSheetDialogFragment() {
    private var lstOfCountries: List<CountryInfoResponse.Country>?=null
    private var lstOfStates: List<StateInfoResponse.State>?=null
    private var countryDialogListener: CountryDialogListener?=null
    private var stateDialogListener:StateDialogListener?=null
    private var whichThing: String?=null

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun newInstance(type: String,payload: Any?): AddressDialogFragment {
            val fragment = AddressDialogFragment()
            val bundle = Bundle()
            val whichThing = type
            bundle.putString("KEY", whichThing)
            if(whichThing.equals("C")){
                val lstOfCountries = payload as ArrayList<CountryInfoResponse.Country>
                bundle.putParcelableArrayList("KEY_", lstOfCountries)
            }
            if(whichThing.equals("S")){
                val lstOfStates = payload as ArrayList<StateInfoResponse.State>
                bundle.putParcelableArrayList("KEY_", lstOfStates)
            }

            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppBottomSheetDialogTheme);
        // collect our intent
        if (arguments != null) {
            whichThing=arguments?.getString("KEY")

            if(whichThing.equals("C")){
                lstOfCountries= arguments?.getParcelableArrayList("KEY_")
            }
            if(whichThing.equals("S")){
                lstOfStates= arguments?.getParcelableArrayList("KEY_")
            }

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_address_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when(whichThing){
            "C" ->  {

                tv_label_one?.text =getString(R.string.title_select_country)
                rv_unit?.apply {
                    layoutManager = GridLayoutManager(activity,1)
                    val countryAdapter= CountryAdapter()
                    adapter = countryAdapter
                    countryAdapter.setOnItemClickListener(object :
                        CountryAdapter.OnItemClickListener {
                        override fun onItemClick(model: CountryInfoResponse.Country, adapterPosition: Int) {
                            countryDialogListener?.onClickCountry(model)
                        }
                    })
                    lstOfCountries?.let { countryAdapter.addAll(it) }
                }
            }
            "S" ->{

                tv_label_one?.text =getString(R.string.title_select_state)
                rv_unit?.apply {
                    layoutManager = GridLayoutManager(activity,1)
                    val stateAdapter= StateAdapter()
                    adapter = stateAdapter
                    stateAdapter.setOnItemClickListener(object :
                        StateAdapter.OnItemClickListener {
                        override fun onItemClick(model: StateInfoResponse.State, adapterPosition: Int) {
                            stateDialogListener?.onClickState(model)
                        }
                    })
                    lstOfStates?.let { stateAdapter.addAll(it) }
                }
            }

        }

    }
    class CountryAdapter : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {
        private var mItemClickListener: OnItemClickListener? = null
        val items: ArrayList<CountryInfoResponse.Country>

        init {
            this.items = ArrayList()
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
            return CountryViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_address, parent, false))
        }



        interface OnItemClickListener {
            fun onItemClick(model: CountryInfoResponse.Country, adapterPosition: Int);
        }

        fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
            this.mItemClickListener = mItemClickListener
        }


        fun add(item: CountryInfoResponse.Country) {
            items.add(item)
            notifyItemInserted(items.size - 1)
        }

        fun addAll(items: List<CountryInfoResponse.Country>) {
            for (item in items) {
                add(item)
            }
        }


        // Update ALL VIEW holder
        override fun onBindViewHolder(holder: CountryViewHolder, pos: Int) {
            val item = items.get(pos)
            val viewHolder = holder
            item.let {
                viewHolder.bindView(item)
                viewHolder.itemView.setOnClickListener {
                    mItemClickListener?.onItemClick(item,0)
                }
            }



        }
        class CountryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bindView(model: CountryInfoResponse.Country) {
                // views
                itemView.tv_name?.text = model.name

            }


        }
    }
    class StateAdapter() : RecyclerView.Adapter<StateAdapter.StateViewHolder>() {
        private var mItemClickListener: OnItemClickListener? = null
        val items: ArrayList<StateInfoResponse.State>

        init {
            this.items = ArrayList()
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateViewHolder {
            return StateViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_address, parent, false))
        }



        interface OnItemClickListener {
            fun onItemClick(model: StateInfoResponse.State, adapterPosition: Int);
        }

        fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
            this.mItemClickListener = mItemClickListener
        }


        fun add(item: StateInfoResponse.State) {
            items.add(item)
            notifyItemInserted(items.size - 1)
        }

        fun addAll(items: List<StateInfoResponse.State>) {
            for (item in items) {
                add(item)
            }
        }


        // Update ALL VIEW holder
        override fun onBindViewHolder(holder: StateViewHolder, pos: Int) {
            val item = items.get(pos)
            val viewHolder = holder
            item.let {
                viewHolder.bindView(item)
                viewHolder.itemView.setOnClickListener {
                    mItemClickListener?.onItemClick(item,0)
                }
            }



        }
        class StateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bindView(model: StateInfoResponse.State) {
                // views
                itemView.tv_name?.text = model.name

            }


        }
    }

    fun setOnCountryDialogListener(listener: CountryDialogListener?) {
        this.countryDialogListener = listener
    }

    fun setOnStateDialogListener(listener: StateDialogListener?) {
        this.stateDialogListener = listener
    }

    public interface CountryDialogListener {
        fun onClickCountry(countryInfo: CountryInfoResponse.Country)
    }

    public interface StateDialogListener {
        fun onClickState(stateInfo:StateInfoResponse.State)
    }
}