package com.example.hw2;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class GameOverActivity extends AppCompatActivity {

    public static final String EXTRA_PLAYER = "GameOverActivity.EXTRA_PLAYER";
    public static final String EXTRA_PLAYER_NAME = "GameOverActivity.EXTRA_PLAYER_NAME";
    private Player player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

         GetIntent();

        TextView scoreView= findViewById(R.id.score_txt);
        scoreView.setText("score : "+player.getScore());

        TextView lifeView= findViewById(R.id.life_txt);
        lifeView.setText("life : "+player.getLife());

        TextView isWinnerView= findViewById(R.id.is_winner);
        String txt=String.format("%s %s",player.getPlayerName(),player.getIsWinner() ? "win !!!":"lose :(");
        isWinnerView.setText(txt);


    }


    //get intent from GameActivity
    private void GetIntent() {
        Intent intent = getIntent();
        player= (Player) intent.getSerializableExtra(GameActivity.EXTRA_PLAYER);
    }

    public void ClickNewGame(View view) {
        Intent intent = new Intent(this,GameActivity.class);
        intent.putExtra(EXTRA_PLAYER_NAME, player.getPlayerName());
        startActivity(intent);
    }

    public void ClickTopTen(View view) {
        Intent intent = new Intent(this,TopTenActivity.class);
        intent.putExtra(EXTRA_PLAYER,player);
        startActivity(intent);
    }

}


