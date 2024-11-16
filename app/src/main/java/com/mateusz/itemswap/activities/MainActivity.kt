package com.mateusz.itemswap.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mateusz.itemswap.R
import com.mateusz.itemswap.fragment.AccountFragment
import com.mateusz.itemswap.fragment.FindFragment
import com.mateusz.itemswap.fragment.FollowedFragment
import com.mateusz.itemswap.fragment.MessagesFragment

class MainActivity : AppCompatActivity() {
    
    private lateinit var bottomNavigation: BottomNavigationView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        initFragmentNavigation()
    }

    private fun initFragmentNavigation() {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, FindFragment()).commit()

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_find -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, FindFragment()).commit()
                    true
                }
                R.id.nav_followed -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, FollowedFragment()).commit()
                    true
                }
                R.id.nav_add -> {
                    val intent = Intent(this, AddActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_messages -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MessagesFragment()).commit()
                    true
                }
                R.id.nav_account -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AccountFragment()).commit()
                    true
                }
                else -> false
            }
        }
    }
}