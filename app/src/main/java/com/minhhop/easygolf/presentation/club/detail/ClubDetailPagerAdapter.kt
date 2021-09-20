package com.minhhop.easygolf.presentation.club.detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ClubDetailPagerAdapter(fA:FragmentActivity, private val listFragment:ArrayList<Fragment>) :  FragmentStateAdapter(fA){

    override fun getItemCount(): Int = listFragment.size

    override fun createFragment(position: Int): Fragment = listFragment[position]



}