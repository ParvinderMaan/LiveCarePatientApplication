package com.app.patlivecare.profile.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.patlivecare.R
import com.app.patlivecare.profile.model.HeightInfo
import com.app.patlivecare.profile.model.WeightInfo

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_unit_convertor.*
import kotlinx.android.synthetic.main.list_item_unit.view.*

class UnitConvertorDialogFragment : BottomSheetDialogFragment() {

    private var heightDialogListener: HeightDialogListener?=null
    private var weightDialogListener: WeightDialogListener?=null

    private var whichThing: String?=null

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun newInstance(payload: Any?): UnitConvertorDialogFragment {
            val fragment = UnitConvertorDialogFragment()
            val bundle = Bundle()
            val whichThing = payload as String
            bundle.putString("KEY", whichThing)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppBottomSheetDialogTheme);

        // collect our intent
        if (arguments != null) {
            whichThing = arguments?.getString("KEY")
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_unit_convertor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when(whichThing){
           "HEIGHT" ->  {
               tv_label_one?.text = getString(R.string.title_select_height)
               tv_unit_one?.text = getString(R.string.title_cms)
               tv_unit_two?.text = getString(R.string.title_feet)
               iv_unit_one?.isSelected=true
               iv_unit_two?.isSelected=false
               iv_unit_one?.setOnClickListener {
                   iv_unit_one?.isSelected=true
                   iv_unit_two?.isSelected=false
                   rv_unit?.apply {
                       layoutManager = GridLayoutManager(activity, 4)
                       val heightAdapter=HeightAdapter()
                       adapter = heightAdapter
                       heightAdapter.setOnItemClickListener(object :
                           HeightAdapter.OnItemClickListener {
                           override fun onItemClick(model: HeightInfo, adapterPosition: Int) {
                               val height:Double = model.unit.toDouble()
                               heightDialogListener?.onClickHeight(height)

                           }

                       })
                       heightAdapter.addAll(getHeightInCm())
                   }

               }
               iv_unit_two?.setOnClickListener {
                   iv_unit_one?.isSelected=false
                   iv_unit_two?.isSelected=true
                   rv_unit?.apply {
                       layoutManager = GridLayoutManager(activity, 4)
                       val heightAdapter=HeightAdapter()
                       adapter = heightAdapter
                       heightAdapter.setOnItemClickListener(object :
                           HeightAdapter.OnItemClickListener {
                           override fun onItemClick(model: HeightInfo, adapterPosition: Int) {
                               val height = model.unit.replace("\"","")
                               val feet=height.split("'")[0]
                               val inch=height.split("'")[1]
                               val heightInCm=(feet.toDouble() * 30.48) + (inch.toDouble() * 2.54)
                               heightDialogListener?.onClickHeight(heightInCm)
                           }
                       })
                       heightAdapter.addAll(getHeightInFeet())
                   }
               }
               rv_unit?.apply {
                   layoutManager = GridLayoutManager(activity, 4)
                   val heightAdapter=HeightAdapter()
                   adapter = heightAdapter
                   heightAdapter.setOnItemClickListener(object :
                       HeightAdapter.OnItemClickListener {
                       override fun onItemClick(model: HeightInfo, adapterPosition: Int) {
                           val height:Double = model.unit.toDouble()
                           heightDialogListener?.onClickHeight(height)
                       }
                   })
                   heightAdapter.addAll(getHeightInCm())
               }
           }
           "WEIGHT" ->{
               tv_label_one?.text =getString(R.string.title_select_weight)
               tv_unit_one?.text = getString(R.string.title_kgs)
               tv_unit_two?.text = getString(R.string.title_lbs)
               iv_unit_one?.isSelected=true
               iv_unit_two?.isSelected=false
               iv_unit_one?.setOnClickListener {
                   iv_unit_one?.isSelected=true
                   iv_unit_two?.isSelected=false
                   rv_unit?.apply {
                       layoutManager = GridLayoutManager(activity, 4)
                       val weightAdapter=WeightAdapter()
                       adapter = weightAdapter
                       weightAdapter.setOnItemClickListener(object :
                           WeightAdapter.OnItemClickListener {
                           override fun onItemClick(model: WeightInfo, adapterPosition: Int) {
                               val weight:Double = model.unit.toDouble()
                               val weightInLbs=(weight * 2.20462262185)
                               weightDialogListener?.onClickWeight(weightInLbs)

                           }

                       })
                       weightAdapter.addAll(getWeightInKg())
                   }
               }
               iv_unit_two?.setOnClickListener {
                   iv_unit_one?.isSelected=false
                   iv_unit_two?.isSelected=true
                   rv_unit?.apply {
                       layoutManager = GridLayoutManager(activity, 4)
                       val weightAdapter=WeightAdapter()
                       adapter = weightAdapter
                       weightAdapter.setOnItemClickListener(object :
                           WeightAdapter.OnItemClickListener {
                           override fun onItemClick(model: WeightInfo, adapterPosition: Int) {
                               var weight:Double = model.unit.toDouble()
                               weightDialogListener?.onClickWeight(weight)

                           }

                       })
                       weightAdapter.addAll(getWeightInLb())
                   }
               }
               rv_unit?.apply {
                   layoutManager = GridLayoutManager(activity, 4)
                   val weightAdapter=WeightAdapter()
                   adapter = weightAdapter
                   weightAdapter.setOnItemClickListener(object :
                       WeightAdapter.OnItemClickListener {
                       override fun onItemClick(model: WeightInfo, adapterPosition: Int) {
                           val weight:Double = model.unit.toDouble()
                           val weightInLbs=(weight * 2.20462262185)
                           weightDialogListener?.onClickWeight(weightInLbs)
                       }
                   })
                   weightAdapter.addAll(getWeightInKg())
               }
           }

        }

    }


    class HeightAdapter : RecyclerView.Adapter<HeightAdapter.HeightViewHolder>() {
        private var mItemClickListener: OnItemClickListener? = null
        val items: ArrayList<HeightInfo>

        init {
            this.items = ArrayList()
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeightViewHolder {
            return HeightViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_unit, parent, false))
        }



        interface OnItemClickListener {
            fun onItemClick(model: HeightInfo, adapterPosition: Int);
        }

        fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
            this.mItemClickListener = mItemClickListener
        }


        fun add(item: HeightInfo) {
            items.add(item)
            notifyItemInserted(items.size - 1)
        }

        fun addAll(items: List<HeightInfo>) {
            for (item in items) {
                add(item)
            }
        }


        // Update ALL VIEW holder
        override fun onBindViewHolder(holder: HeightViewHolder, pos: Int) {
            val item = items.get(pos)
            val viewHolder = holder
            item.let {
                viewHolder.bindView(item)
                viewHolder.itemView.setOnClickListener {
                    mItemClickListener?.onItemClick(item,0)
                }
            }

            // listener


        }
        class HeightViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bindView(model: HeightInfo) {
                // views
                itemView.tv_name?.text = model.unit

                itemView.tv_name.isSelected= model.isSelected
            }


        }
    }
    class WeightAdapter() : RecyclerView.Adapter<WeightAdapter.WeightViewHolder>() {
        private var mItemClickListener: OnItemClickListener? = null
        val items: ArrayList<WeightInfo>

        init {
            this.items = ArrayList()
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightViewHolder {
            return WeightViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_unit, parent, false))
        }



        interface OnItemClickListener {
            fun onItemClick(model: WeightInfo, adapterPosition: Int);
        }

        fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
            this.mItemClickListener = mItemClickListener
        }


        fun add(item: WeightInfo) {
            items.add(item)
            notifyItemInserted(items.size - 1)
        }

        fun addAll(items: List<WeightInfo>) {
            for (item in items) {
                add(item)
            }
        }


        // Update ALL VIEW holder
        override fun onBindViewHolder(holder: WeightViewHolder, pos: Int) {
            val item = items.get(pos)
            val viewHolder = holder
            item.let {
                viewHolder.bindView(item)
                viewHolder.itemView.setOnClickListener {
                    mItemClickListener?.onItemClick(item,0)
                }
            }



        }
        class WeightViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bindView(model: WeightInfo) {
                // views
                itemView.tv_name?.text = model.unit

                itemView.tv_name.isSelected=false
            }


        }
    }



    private fun getHeightInCm(): List<HeightInfo> {
        val lstOfHeight: ArrayList<HeightInfo> = ArrayList()
        for (i in 100..300) {
            lstOfHeight.add(HeightInfo(i.toString(),false))
        }
        return lstOfHeight
    }

    private fun getHeightInFeet(): List<HeightInfo> {
        val lstOfHeight: ArrayList<HeightInfo> = ArrayList()
        lstOfHeight.add(HeightInfo("4'0\"",false))
        lstOfHeight.add(HeightInfo("4'1\"",false))
        lstOfHeight.add(HeightInfo("4'2\"",false))
        lstOfHeight.add(HeightInfo("4'3\"",false))
        lstOfHeight.add(HeightInfo("4'4\"",false))
        lstOfHeight.add(HeightInfo("4'5\"",false))
        lstOfHeight.add(HeightInfo("4'6\"",false))
        lstOfHeight.add(HeightInfo("4'7\"",false))
        lstOfHeight.add(HeightInfo("4'8\"",false))
        lstOfHeight.add(HeightInfo("4'9\"",false))
        lstOfHeight.add(HeightInfo("4'10\"",false))
        lstOfHeight.add(HeightInfo("4'11\"",false))
        lstOfHeight.add(HeightInfo("5'0\"",false))
        lstOfHeight.add(HeightInfo("5'1\"",false))
        lstOfHeight.add(HeightInfo("5'2\"",false))
        lstOfHeight.add(HeightInfo("5'3\"",false))
        lstOfHeight.add(HeightInfo("5'4\"",false))
        lstOfHeight.add(HeightInfo("5'5\"",false))
        lstOfHeight.add(HeightInfo("5'6\"",false))
        lstOfHeight.add(HeightInfo("5'7\"",false))
        lstOfHeight.add(HeightInfo("5'8\"",false))
        lstOfHeight.add(HeightInfo("5'9\"",false))
        lstOfHeight.add(HeightInfo("5'10\"",false))
        lstOfHeight.add(HeightInfo("5'11\"",false))
        lstOfHeight.add(HeightInfo("6'0\"",false))
        lstOfHeight.add(HeightInfo("6'1\"",false))
        lstOfHeight.add(HeightInfo("6'2\"",false))
        lstOfHeight.add(HeightInfo("6'3\"",false))
        lstOfHeight.add(HeightInfo("6'4\"",false))
        lstOfHeight.add(HeightInfo("6'5\"",false))
        lstOfHeight.add(HeightInfo("6'6\"",false))
        lstOfHeight.add(HeightInfo("6'7\"",false))
        lstOfHeight.add(HeightInfo("6'8\"",false))
        lstOfHeight.add(HeightInfo("6'9\"",false))
        lstOfHeight.add(HeightInfo("6'10\"",false))
        lstOfHeight.add(HeightInfo("6'11\"",false))
        lstOfHeight.add(HeightInfo("7'0\"",false))
        return lstOfHeight
    }

    private fun getWeightInKg(): List<WeightInfo> {
        val lstOfWeight: ArrayList<WeightInfo> = ArrayList()
        for (i in 40..150) {
            lstOfWeight.add(WeightInfo(i.toString(),false))
        }
        return lstOfWeight
    }

    private fun getWeightInLb(): List<WeightInfo> {
        val lstOfWeight: ArrayList<WeightInfo> = ArrayList()
        for (i in 80..300) {
            lstOfWeight.add(WeightInfo(i.toString(),false))
        }
        return lstOfWeight
    }

    fun setOnHeightDialogListener(listener: HeightDialogListener?) {
        this.heightDialogListener = listener
    }

    fun setOnWeightDialogListener(listener: WeightDialogListener?) {
        this.weightDialogListener = listener
    }

    public interface HeightDialogListener {
        fun onClickHeight(height: Double)
    }

    public interface WeightDialogListener {
        fun onClickWeight(weight:Double)
    }


}