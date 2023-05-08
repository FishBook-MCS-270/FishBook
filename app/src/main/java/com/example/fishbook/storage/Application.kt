package com.example.fishbook.storage

import android.app.Application
import com.google.firebase.FirebaseApp
import com.squareup.picasso.*
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        DataRepository.initialize(this)
        FirebaseApp.initializeApp(this)

        // Memory cache configuration
        val cacheSize = 50 * 1024 * 1024 // 50 MB
        val memoryCache = LruCache(cacheSize)

        // Disk cache configuration
        val cacheDir = File(cacheDir, "picasso-cache")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        val httpClient = OkHttpClient.Builder()
            .cache(Cache(cacheDir, cacheSize.toLong()))
            .build()

        // Create custom Picasso instance with configured memory and disk cache
        val customPicasso = Picasso.Builder(this)
            .memoryCache(memoryCache)
            .downloader(OkHttp3Downloader(httpClient))
            .build()

        // Set the custom Picasso instance as the singleton instance
        Picasso.setSingletonInstance(customPicasso)
    }
}