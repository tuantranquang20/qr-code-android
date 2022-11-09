package com.base.common.base.adapter

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

open class ViewPagerAdapter(fm: FragmentManager, private val fragmentList: List<Fragment>, private val titles:
List<String>) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return this.fragmentList[position]
    }

    override fun getCount(): Int {
        return this.fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return this.titles[position]
    }

    override fun saveState(): Parcelable? {
        return null
    }
}