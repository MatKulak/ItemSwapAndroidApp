package com.mateusz.itemswap.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mateusz.itemswap.fragment.MyAdvertisementsFragment
import com.mateusz.itemswap.fragment.MyFollowedAdvertisementsFragment

class FollowedPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MyAdvertisementsFragment()
            1 -> MyFollowedAdvertisementsFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}