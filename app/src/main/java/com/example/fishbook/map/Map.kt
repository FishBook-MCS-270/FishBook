package com.example.fishbook.map

import MapViewModel
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.fishbook.R
import com.example.fishbook.storage.DataRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class Map : Fragment() {

    private val mapViewModel: MapViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        lifecycleScope.launch {
            setupMap(view)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        Configuration.getInstance().load(
            activity, PreferenceManager.getDefaultSharedPreferences(
                activity
            )
        )
    }

private suspend fun setupMap(view: View) {

    val ctx = requireActivity().applicationContext
    Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
    val map = view.findViewById<MapView>(R.id.map)

    map.setUseDataConnection(true)

    map.setTileSource(TileSourceFactory.MAPNIK)
    map.setBuiltInZoomControls(true)
    map.setMultiTouchControls(true)
    val mapController = map.controller

    //Set Minnesota Coordinates
    mapController.setZoom(8.1)
    val startPoint = GeoPoint(46.7296, -94.6859)
    mapController.setCenter(startPoint)

    //Set Zoom Limit
    map.minZoomLevel = 3.0
    map.maxZoomLevel = 19.0

    //Set Current Location
    val mGpsMyLocationProvider = GpsMyLocationProvider(activity)
    mGpsMyLocationProvider.locationUpdateMinDistance
    mGpsMyLocationProvider.locationUpdateMinTime

    val myLocationNewOverlay = MyLocationNewOverlay(mGpsMyLocationProvider, map)
    myLocationNewOverlay.enableMyLocation()
    myLocationNewOverlay.enableFollowLocation()
    myLocationNewOverlay.isDrawAccuracyEnabled

    //map.overlays.add(myLocationNewOverlay)

    //Set Scale Bar
    val mScaleBarOverlay = ScaleBarOverlay(map)
    mScaleBarOverlay.setScaleBarOffset(resources.displayMetrics.widthPixels / 2, 10)
    mScaleBarOverlay.setAlignBottom(true)
    map.overlays.add(mScaleBarOverlay)

    //Support for Map Rotation
    val mRotationGestureOverlay = RotationGestureOverlay(map)
    mRotationGestureOverlay.isEnabled

    //Populate Marker for catches

    val repository = DataRepository.get()
    val catches = mapViewModel.userId?.let { repository.getLocalCatchDetails(it).first() }

    if (catches != null) {
        for (catch in catches) {
            if (catch.latitude != null && catch.longitude != null && catch.latitude.trim().isNotEmpty() && catch.longitude.trim().isNotEmpty()) {
                var catchMarker = Marker(map) //new marker for each
                catchMarker.icon = ContextCompat.getDrawable(ctx, R.drawable.fish_24)
                catchMarker.title = catch.species
                catchMarker.snippet = "Length: ${catch.length} in, Weight: ${catch.weight} lb"

                catchMarker.position = GeoPoint(catch.latitude.toDouble(), catch.longitude.toDouble())
                catchMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                map.overlays.add(catchMarker)
            }
        }
    }
}


}