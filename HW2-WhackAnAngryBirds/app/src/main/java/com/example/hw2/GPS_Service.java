package com.example.hw2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import static java.lang.Thread.sleep;


public class GPS_Service extends Service {
    public static final String EXTRA_LOCATION_Y = "GPS_Service.EXTRA_LOCATION_Y";
    public static final String EXTRA_LOCATION_X = "GPS_Service.EXTRA_LOCATION_X";
    private LocationListener listener;
    private LocationManager locationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("GPS", "onLocationChanged: "+location);
                Intent i = new Intent("location_update");
                i.putExtra(EXTRA_LOCATION_X,location.getLatitude());
                i.putExtra(EXTRA_LOCATION_Y,  location.getLongitude());
                sendBroadcast(i);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

               final Toast toast = Toast.makeText(getApplicationContext(), "Please enable GPS location ...", Toast.LENGTH_LONG);
               View view = toast.getView();
               view.setBackgroundColor(Color.parseColor("#ff33b5e5"));
               toast.show();


                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        Log.d("GPS CURRENT LOCATION", " ");
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, listener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
    }
}
