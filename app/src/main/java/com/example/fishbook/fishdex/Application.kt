package com.example.fishbook.fishdex

import android.app.Application
import com.google.firebase.FirebaseApp

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        DataRepository.initialize(this)
        FirebaseApp.initializeApp(this)

    }
}