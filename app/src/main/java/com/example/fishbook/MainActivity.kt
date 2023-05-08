package com.example.fishbook

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.fishbook.LakeData.Lake
import com.example.fishbook.storage.DataRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.first
import kotlin.coroutines.suspendCoroutine
import kotlin.coroutines.resume

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        DataRepository.initialize(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Setup the bottom navigation view with the NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // USed to handle nav_bar navigations
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.gallery -> {
                    if (navController.currentDestination?.id == R.id.editRecordFragment ||
                        navController.currentDestination?.id == R.id.addRecordFragment ||
                        navController.currentDestination?.id == R.id.viewRecordFragment
                    ) {
                        navController.popBackStack(R.id.gallery, false)
                    } else if (navController.currentDestination?.id != R.id.gallery) {
                        navController.navigate(R.id.gallery)
                    }
                    true
                }
                R.id.fishdex -> {
                    if (navController.currentDestination?.id == R.id.speciesFragment) {
                        navController.popBackStack(R.id.fishdex, false)
                    } else if (navController.currentDestination?.id != R.id.fishdex) {
                        navController.navigate(R.id.fishdex)
                    }
                    true
                }
                R.id.map -> {
                    if (navController.currentDestination?.id != R.id.map) {
                        navController.navigate(R.id.map)
                    }
                    true
                }
                else -> false
            }
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

    }

    @Suppress("MemberVisibilityCanBePrivate") //setup as Coroutine to use in other apps
    suspend fun fetchLocation(): Pair<Double, Double> = suspendCoroutine { continuation ->
        val task = fusedLocationProviderClient.lastLocation

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
            Log.d(TAG, "Request Permission")
        }
        task.addOnSuccessListener {
            if(it != null){
                Log.d(TAG, "Current Location, latitude: " + it.latitude + ", longitude: " + it.longitude)
                continuation.resume(Pair(it.latitude, it.longitude))
            }
        }
    }
    //used to calculate distance between points


    @Suppress("MemberVisibilityCanBePrivate")
    suspend fun findNearestLakes(topLakes: Int = 5, fishSpecies: String? = null): List<Pair<Lake, Double>> {
        val repository = DataRepository.get()
        val lakes = repository.getAllLakes().first()
        val (latitude, longitude) = fetchLocation() //needed to add return type

        val lakesDistance = mutableListOf<Pair<Lake, Double>>()

        for (lake in lakes) {
            if (lake.gps_lat != null && lake.gps_long != null) {
                if (fishSpecies == null || lake.fishdexList?.contains(fishSpecies) == true) {
                    val distance = haversine(latitude, longitude, lake.gps_lat, lake.gps_long)
                    lakesDistance.add(Pair(lake, distance))
                }
            }
        }

        lakesDistance.sortBy { it.second }
        val topNearestLakes = lakesDistance.take(topLakes)

        topNearestLakes.forEach{ lakeDistance ->
            Log.d(TAG, "${lakeDistance.first.lakeName}, ${lakeDistance.first.county}, ${lakeDistance.first.gps_lat}, \"${lakeDistance.first.gps_long},${lakeDistance.second} miles")
        }
        return topNearestLakes

    }

    fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 3958.8 // Radius of the earth in miles
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)

        val a = (sin(latDistance / 2) * sin(latDistance / 2)
                + (cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
                * sin(lonDistance / 2) * sin(lonDistance / 2)))
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        val distanceInMiles = R * c
        return String.format("%.1f", distanceInMiles).toDouble()
    }

}



