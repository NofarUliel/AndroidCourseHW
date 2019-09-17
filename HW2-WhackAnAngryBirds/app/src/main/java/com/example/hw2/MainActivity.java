package com.example.hw2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


public class MainActivity extends FragmentActivity {
    public static final String EXTRA_PLAYER = "MainActivity.EXTRA_PLAYER";
    public static final int REQUEST_CODE = 100;
    private Button start_game_btn;
    private Player player;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start_game_btn = findViewById(R.id.start_game_btn);
        start_game_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                openGameActivity();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        //when have permissions start GPS_Service
        if(!runtime_permissions()){
            Log.d("GPS SERVICE", "start gps service");
            Intent i =new Intent(getApplicationContext(),GPS_Service.class);
            startService(i);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void openGameActivity() {
        //when have permissions start GPS_Service
        if(!runtime_permissions()){
            Log.d("GPS SERVICE", "start gps service");
            Intent i =new Intent(getApplicationContext(),GPS_Service.class);
            startService(i);
            EditText editText = findViewById(R.id.player_name);
            String playerName = editText.getText().toString();
            player = new Player(playerName);
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra(EXTRA_PLAYER, player);
            startActivity(intent);
        }
   }


    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_CODE);
            return true;
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            }else {
                runtime_permissions();
            }
        }
    }

}

//  private void IsEnableGps() {
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//        if (!gpsEnabled) {
//            isGPS=false;
//            Log.d("location", "onStart: location not enable");
//            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
//            builder1.setMessage("Please enable location");
//            builder1.setCancelable(true);
//
//            builder1.setPositiveButton(
//                    "Ok",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.cancel();
//                        }
//                    });
//
//            AlertDialog alert11 = builder1.create();
//            alert11.show();
//        } else {
//            isGPS=true;
//            Log.d("location", "onStart: location enable");
//        }
//
//    }
