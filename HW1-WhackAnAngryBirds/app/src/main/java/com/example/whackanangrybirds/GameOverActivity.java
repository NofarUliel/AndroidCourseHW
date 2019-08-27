package com.example.whackanangrybirds;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity {

    public static final String EXTRA_PLAYER_NAME = "GameOverActivity.EXTRA_PLAYER_NAME";
    private String playerName;
    private int score;
    private int life;
    private boolean isWinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        //get player name from main activity
        Intent intent = getIntent();
        playerName = intent.getStringExtra(GameActivity.EXTRA_PLAYER_NAME);
        score = intent.getIntExtra(GameActivity.EXTRA_PLAYER_SCORE,0);
        life = intent.getIntExtra(GameActivity.EXTRA_PLAYER_LIFE,0);
        isWinner = intent.getBooleanExtra(GameActivity.EXTRA_PLAYER_IS_WINNER,false);

        TextView scoreView= findViewById(R.id.score_txt);
        scoreView.setText("score : "+score);

        TextView lifeView= findViewById(R.id.life_txt);
        lifeView.setText("life : "+life);

        TextView isWinnerView= findViewById(R.id.is_winner);
        String txt=String.format("%s %s",playerName,isWinner ? "win !!!":"lose :(");
        isWinnerView.setText(txt);

    }

    public void ClickNewGame(View view) {
        Intent intent = new Intent(this,GameActivity.class);
        intent.putExtra(EXTRA_PLAYER_NAME, playerName);
        startActivity(intent);
    }
}
