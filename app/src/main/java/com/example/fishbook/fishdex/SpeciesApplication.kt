package com.example.fishbook.fishdex

import android.app.Application

class SpeciesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SpeciesRepository.initialize(this)
    }
}