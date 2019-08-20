package com.example.whackanangrybirds;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_TEXT = "MainActivity.EXTRA_TEXT";
    private Button start_game_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start_game_btn = findViewById(R.id.start_game_btn);
        start_game_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                openGameActivity();
            }
        });
    }

    public void openGameActivity(){

        EditText editText =  findViewById(R.id.player_name);
        String playerName = editText.getText().toString();

        Intent intent = new Intent(this,GameActivity.class);
        intent.putExtra(EXTRA_TEXT, playerName);
        startActivity(intent);

    }
}
