package com.example.fishbook.map;

import android.content.Context;

import android.os.Bundle;
import android.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import androidx.fragment.app.Fragment;

import com.example.fishbook.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class Map extends Fragment {

    private MapView map = null;
    private MapController mapController;
    private MyLocationNewOverlay locationOverlay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        map = view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        mapController = (MapController) map.getController();

        //Set Minnesota Coordinates
        mapController.setZoom(8.1);
        GeoPoint startPoint = new GeoPoint(46.7296, -94.6859);
        mapController.setCenter(startPoint);

        //Set-up Current Location
        locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), map);
        locationOverlay.enableFollowLocation();
        locationOverlay.enableMyLocation();
        map.getOverlays().add(locationOverlay);

        map.invalidate();
        return view;
    }
    public void onResume(){
        super.onResume();
        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));
    }
    public void onPause(){
        super.onPause();
        locationOverlay.disableMyLocation();
    }

}
