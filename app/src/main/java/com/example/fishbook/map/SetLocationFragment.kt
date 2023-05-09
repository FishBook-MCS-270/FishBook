package com.example.fishbook.map

//import android.R

import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fishbook.R
import com.example.fishbook.databinding.FragmentSetLocationBinding
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker


class SetLocation : Fragment() {
    private lateinit var binding: FragmentSetLocationBinding
    private var markerAdded = false // add this variable to track marker
    private var marker: Marker? = null

    private lateinit var startPoint: GeoPoint
    private val locationBundle = Bundle()

    // stores markers latitude and longitude
    private var markerLat = 0.0
    private var markerLong = 0.0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetLocationBinding.inflate(layoutInflater, container, false)

        val view = binding.root
        val ctx = requireActivity().applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        val map = view.findViewById<MapView>(R.id.setLocationMap)
        map.setTileSource(TileSourceFactory.MAPNIK)
        //map.setBuiltInZoomControls(true)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)

        map.setMultiTouchControls(true)
        val mapController = map.controller

        // fetch latitude and longitude from add record fragment
        val latitude = arguments?.getDouble("latitude")
        val longitude = arguments?.getDouble("longitude")

        mapController.setZoom(8.1)
        // sets startPoint
        //Set Minnesota Coordinates
        startPoint = GeoPoint(46.7296, -94.6859)

        if (latitude != null && longitude != null && latitude != 0.0 && longitude != 0.0) {
            startPoint = GeoPoint(latitude, longitude)
            mapController.setZoom(15.1)
        }
        Log.i("Map", "Start point latitude: ${startPoint.latitude}, Start point longitude: ${startPoint.longitude}")

        mapController.setCenter(startPoint)

        map.overlays.add(MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(point: GeoPoint): Boolean {
                if (!markerAdded) { // check if marker is already added

                    marker = Marker(map)
                    // set position of marker
                    marker!!.position = point
                    marker!!.icon = resources.getDrawable(R.drawable.baseline_add_location_alt_24)

                    // add marker on map
                    map.overlays.add(marker)
                    Toast.makeText(requireContext(), "Tapped", Toast.LENGTH_SHORT).show()
                    //Log.i("Map", "Initial--- Latitude: ${point.latitude}, Longitude: ${point.longitude}")

                    markerAdded = true
                } else {
                    // update position of marker
                    marker!!.position = point
                    marker!!.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    Toast.makeText(requireContext(), "Marker updated", Toast.LENGTH_SHORT).show()
                    //Log.i("Map", "Updated--- Latitude: ${point.latitude}, Longitude: ${point.longitude}")
                }
                // save latest marker position
                markerLat = point.latitude
                markerLong = point.longitude
                Log.i("Map", "Marker Lat: $markerLat, Marker Long: $markerLong")
                return false
            }


            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }))

        // saves marker latitude and longitude to location bundle
        binding.saveButton.setOnClickListener {
            locationBundle.apply {
                putString("markerLatitude", markerLat.toString())
                putString("markerLongitude", markerLong.toString())
                findNavController().navigate(R.id.addRecordFragment, locationBundle)
            }
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
}