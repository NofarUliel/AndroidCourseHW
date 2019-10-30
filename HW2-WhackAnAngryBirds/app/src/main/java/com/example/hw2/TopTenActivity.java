package com.example.hw2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TopTenActivity extends AppCompatActivity {
    public static final String EXTRA_PLAYER_DB = "TopTenActivity.EXTRA_PLAYER_DB";

    private DatabaseHelper _DatabaseHelper;
    private Player player;
    private ArrayList<Player> playerDB=new ArrayList<>();
    private BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_ten);



        Intent intent = getIntent();
        player= (Player) intent.getSerializableExtra(GameOverActivity.EXTRA_PLAYER);

        _DatabaseHelper = new DatabaseHelper(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        broadcastReceiver=null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            Toast toast = Toast.makeText(getApplicationContext(), "fetching location ...", Toast.LENGTH_SHORT);
            toast.show();
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    player.setLocation_x((Double) intent.getExtras().get(GPS_Service.EXTRA_LOCATION_X));
                    player.setLocation_y((Double) intent.getExtras().get(GPS_Service.EXTRA_LOCATION_Y));

                    addPlayerToDB();
                    getDataFromDB();
                    displayDataFromDB();
                    unregisterReceiver(broadcastReceiver);

                }
            };
        }




        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stop GPS Service
        Intent i = new Intent(getApplicationContext(),GPS_Service.class);
        stopService(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }

    }
    private void addPlayerToDB(){
        if (_DatabaseHelper.addData(player.getPlayerName(), player.getScore(), player.getLocation_x(),player.getLocation_y())) {
            Log.d("ADD TO DB", "onCreate: success");
            Log.d("ADD TO DB LOCATION", "("+player.getLocation_x()+","+player.getLocation_y()+")");
        } else {
            Log.d("ADD TO DB", "onCreate: fail");
        }
    }
    private void getDataFromDB() {

        Cursor data=_DatabaseHelper.getData();
        Log.d("GET DATA FROM DB", "getDataFromDB: ");
        while (data.moveToNext()) {

            Player player = new Player(data.getString(1), data.getInt(2), data.getDouble(3), data.getDouble(4));
            playerDB.add(player);
        }
    }
    public void ClickMapView(View view) {
        Intent intent = new Intent(this,MapsActivity.class);
        intent.putExtra(EXTRA_PLAYER_DB, playerDB);
        startActivity(intent);

    }
    public void displayDataFromDB() {
        Log.d("DISPLAY DB", "displayDataFromDB: start");


        TableLayout table = findViewById(R.id.table_layout);
        TextView index, player_name, score, index_title, name_title, score_title;


        TableRow titles = new TableRow(this);
        titles.setBackgroundColor(Color.parseColor("#81d4fa"));
        index_title = new TextView(this);
        name_title = new TextView(this);
        score_title = new TextView(this);

        index_title.setPadding(0, 0, 40, 0);
        name_title.setPadding(0, 0, 40, 0);
        score_title.setPadding(0, 0, 40, 0);

        index_title.setGravity(View.TEXT_ALIGNMENT_CENTER);
        name_title.setGravity(View.TEXT_ALIGNMENT_CENTER);
        score_title.setGravity(View.TEXT_ALIGNMENT_CENTER);

        name_title.setText(" ");
        name_title.setText("NAME");
        score_title.setText("SCORE");

        name_title.setTextSize(15f);
        name_title.setTextSize(15f);
        score_title.setTextSize(15f);

        titles.addView(index_title);
        titles.addView(name_title);
        titles.addView(score_title);
        table.addView(titles);


        for (int i = 0; i < playerDB.size(); i++) {

            index = new TextView(this);
            index.setPadding(0, 0, 40, 0);
            index.setGravity(View.TEXT_ALIGNMENT_CENTER);
            index.setText(i + 1 + "");
            index.setTextSize(15f);


            player_name = new TextView(this);
            player_name.setPadding(0, 0, 40, 0);
            player_name.setGravity(View.TEXT_ALIGNMENT_CENTER);
            player_name.setText(playerDB.get(i).getPlayerName());
            player_name.setTextSize(15f);


            score = new TextView(this);
            score.setPadding(0, 0, 40, 0);
            score.setGravity(View.TEXT_ALIGNMENT_CENTER);
            score.setText(playerDB.get(i).getScore() + "");
            score.setTextSize(15f);


            TableRow row = new TableRow(this);
            row.addView(index);
            row.addView(player_name);
            row.addView(score);

            table.addView(row);

        }

    }

}
