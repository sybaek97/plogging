package com.plogging.app.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.plogging.app.view.main.BoardFragment
import com.plogging.app.view.main.HomeFragment
import com.plogging.app.view.main.MypageFragment
import com.plogging.app.view.main.RankFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 4 // 총 Fragment 수
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> RankFragment()
            2 -> BoardFragment()
            3 -> MypageFragment()
            else -> HomeFragment()
        }
    }
}
