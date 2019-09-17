package com.example.hw2;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Player> playerDB=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        Intent intent = getIntent();
        playerDB = (ArrayList<Player>) intent.getSerializableExtra(TopTenActivity.EXTRA_PLAYER_DB);
        Log.d("map player name:", "onCreate: "+playerDB.get(0).getPlayerName());
        Log.d("map player location:", "onCreate: "+playerDB.get(0).getLocation_x()+","+playerDB.get(0).getLocation_y());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for (int i = 0; i < playerDB.size(); i++) {
            // Add a marker in Sydney and move the camera
            LatLng new_location = new LatLng(playerDB.get(i).getLocation_x(), playerDB.get(i).getLocation_y());
            mMap.addMarker(new MarkerOptions().position(new_location).title(playerDB.get(i).toString()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new_location,10f));

        }
    }

}
