package com.example.fishbook.map

import MapViewModel
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.fishbook.LakeData.Lake
import com.example.fishbook.R
import com.example.fishbook.storage.DataRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class Map : Fragment() {

    private val mapViewModel: MapViewModel by viewModels()


    var mOverlayItemList = ArrayList<OverlayItem>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
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
        map.overlays.add(myLocationNewOverlay)

        //Set Scale Bar
        val mScaleBarOverlay = ScaleBarOverlay(map)
        mScaleBarOverlay.setScaleBarOffset(resources.displayMetrics.widthPixels / 2, 10)
        mScaleBarOverlay.setAlignBottom(true)
        map.overlays.add(mScaleBarOverlay)

        //Support for Map Rotation
        val mRotationGestureOverlay = RotationGestureOverlay(map)
        mRotationGestureOverlay.isEnabled
        map.overlays.add(myLocationNewOverlay)

        //Set Compass
        val mCompassOverlay = CompassOverlay(
            activity,
            InternalCompassOrientationProvider(activity),
            map)
        map.overlays.add(mCompassOverlay)

        //Test Marker
        var testMarker = Marker(map)
        testMarker.icon = ContextCompat.getDrawable(ctx, R.drawable.baseline_pin_drop_24)
        testMarker.title = "Test Marker"
        testMarker.position = GeoPoint(46.7296, -94.6859)
        testMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        map.overlays.add(testMarker)

        //Populate Marker for catches
        /*
        suspend fun catchMarker() {
            val repository = DataRepository.get()
            val catches = mapViewModel.userId?.let { repository.getLocalCatchDetails(it).first() }
            var catchMarker = Marker(map)

            for (catch in catches) {
                if (catch.gps_lat != null && catch.gps_long != null) {
                        catchMarker.icon = ContextCompat.getDrawable(ctx, R.drawable.baseline_pin_drop_24)
                        //catchMarker.title =
                        //catchMarker.position = GeoPoint(catch.gps_lat, catch.gps_long)
                        catchMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        map.overlays.add(catchMarker)
                }
            }
        }
        */
        map.invalidate()
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


}