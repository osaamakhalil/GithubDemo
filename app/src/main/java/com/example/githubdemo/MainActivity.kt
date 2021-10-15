package com.example.githubdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navigationBottom by lazy {
            findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        }

        supportActionBar?.hide()

        val navController = findNavController(R.id.myNaveHostFragment)
        navigationBottom.setupWithNavController(navController)


    }
}