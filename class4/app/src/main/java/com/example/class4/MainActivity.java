package com.example.class4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final String FILE_NAME = "preferencesFile";
    private EditText txtName;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         txtName = findViewById(R.id.txt_name);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //access to file
        sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String nameFromDB= sharedPreferences.getString(Keys.PLAYER_NAME,"");
        txtName.setText(nameFromDB);
    }

    @Override
    protected void onStop() {
        super.onStop();

        //save to xml file the player name
        //apply in other thread and commit in the same thread
        sharedPreferences.edit().putString(Keys.PLAYER_NAME,txtName.getText().toString()).apply();

    }
}
