package com.app.patlivecare.interfacor;

import com.app.patlivecare.annotation.FragmentType

interface HomeFragmentSelectedListener {
    fun showFragment(@FragmentType fragmentName:String, payload:Any?=null)
    fun popTillFragment( tag:String,  flag:Int)
    fun popTopMostFragment()
    fun refreshUi(@FragmentType fragmentName:String)


}
