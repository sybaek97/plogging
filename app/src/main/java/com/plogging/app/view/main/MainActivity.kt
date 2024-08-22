package com.plogging.app.view.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.plogging.app.R
import com.plogging.app.adapter.ViewPagerAdapter
import com.plogging.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val TAG = javaClass.simpleName
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewPager = binding.viewPager
        bottomNavigationView = binding.bottomNavigation
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when(position){
                    0 -> bottomNavigationView.selectedItemId = R.id.nav_home
                    1 -> bottomNavigationView.selectedItemId = R.id.nav_rank
                    2 -> bottomNavigationView.selectedItemId = R.id.nav_board
                    3 -> bottomNavigationView.selectedItemId = R.id.nav_mypage
                }
            }
        })
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> viewPager.currentItem = 0
                R.id.nav_rank -> viewPager.currentItem = 1
                R.id.nav_board -> viewPager.currentItem = 2
                R.id.nav_mypage -> viewPager.currentItem = 3
            }
            true
        }


    }
}