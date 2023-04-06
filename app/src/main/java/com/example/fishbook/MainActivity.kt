package com.example.fishbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    private val cameraFragment = Camera()
    private val galleryFragment = Gallery()
    private val fishDexFragment = FishDex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        //set camera as init fragment
        supportFragmentManager.beginTransaction().replace(R.id.container, cameraFragment).commit()

        //replace fragment container on click
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.camera -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container, cameraFragment).commit()
                    true
                }
                R.id.fishdex -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container, fishDexFragment).commit()
                    true
                }

                R.id.gallery -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container, galleryFragment).commit()
                    true
                }
                else -> false
            }
        }
    }
}