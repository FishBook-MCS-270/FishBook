package com.example.fishbook.map

//import android.R

import android.content.ContentValues
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fishbook.R
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker


class SetLocation : Fragment() {
    private var markerAdded = false // add this variable to track marker
    private var marker: Marker? = null

    // saves gps point
    val LAT = ""
    val LONG = ""
    var lat = ""
    var long = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_set_location, container, false)
        val ctx = requireActivity().applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        val map = view.findViewById<MapView>(R.id.setLocationMap)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(true)
        val mapController = map.controller

        //Set Minnesota Coordinates
        mapController.setZoom(8.1)
        val startPoint = GeoPoint(46.7296, -94.6859)
        mapController.setCenter(startPoint)

        map.overlays.add(MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(point: GeoPoint): Boolean {
                if (!markerAdded) { // check if marker is already added
                    marker = Marker(map)
                    // set position of marker
                    marker!!.position = point
                    // add marker on map
                    map.overlays.add(marker)
                    Toast.makeText(requireContext(), "Tapped", Toast.LENGTH_SHORT).show()
                    Log.i("Map", "Initial--- Latitude: ${point.latitude}, Longitude: ${point.longitude}")

                    markerAdded = true
                } else {
                    // update position of marker
                    marker!!.position = point
                    marker!!.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    Toast.makeText(requireContext(), "Marker updated", Toast.LENGTH_SHORT).show()
                    Log.i("Map", "Updated--- Latitude: ${point.latitude}, Longitude: ${point.longitude}")

                }

                lat = point.latitude.toString()
                long = point.longitude.toString()
                return false
            }


            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }))
        return view
    }

    override fun onSaveInstanceState(location: Bundle) {
        super.onSaveInstanceState(location)
        location.putString(LAT, lat)
        location.putString(LONG, long)
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