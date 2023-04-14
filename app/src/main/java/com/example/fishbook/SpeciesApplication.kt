package com.example.fishbook

import android.app.Application

class SpeciesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SpeciesRepository.initialize(this)
    }
}