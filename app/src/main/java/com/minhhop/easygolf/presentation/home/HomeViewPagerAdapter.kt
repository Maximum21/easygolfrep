package com.minhhop.easygolf.presentation.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomeViewPagerAdapter(fm:FragmentActivity,private val listFragment:ArrayList<Fragment>) : FragmentStateAdapter(fm) {

    override fun getItemCount(): Int = listFragment.size

    override fun createFragment(position: Int): Fragment = listFragment[position]
}