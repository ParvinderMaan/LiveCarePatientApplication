package com.app.patlivecare.signup.view

import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter


class SignUpViewPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentStatePagerAdapter(fm, behavior) {
    private val registeredFragments = SparseArray<Fragment>()

    override fun getItem(pos: Int): Fragment {
        var result: Fragment? =null
        when(pos){
            0 -> result=SignUpOneFragment.newInstance()
            1 -> result=SignUpTwoFragment.newInstance()
            2 -> result=SignUpThreeFragment.newInstance()
            3 -> result=SignUpFourFragment.newInstance()
            4 -> result=SignUpFiveFragment.newInstance()
            5 -> result=SignUpSixFragment.newInstance()
        }

         return result!!
    }

    override fun getCount(): Int {
        return 6
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        registeredFragments.put(position, fragment)
        return fragment

    }

    override fun destroyItem(container: ViewGroup, position: Int, objectt: Any) {
        registeredFragments.remove(position)
        super.destroyItem(container, position, objectt)
    }

    fun getRegisteredFragment(position: Int): Fragment? {
        return registeredFragments[position]
    }
}