package com.example.whackanangrybirds;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class GameActivity extends AppCompatActivity {
    public static final String EXTRA_PLAYER_NAME = "GameActivity.EXTRA_PLAYER_NAME";
    public static final String EXTRA_PLAYER_SCORE = "GameActivity.EXTRA_PLAYER_SCORE";
    public static final String EXTRA_PLAYER_LIFE = "GameActivity.EXTRA_PLAYER_LIFE";
    public static final String EXTRA_PLAYER_IS_WINNER = "GameActivity.EXTRA_PLAYER_IS_WINNER";

    private final int COL_SIZE = 3;
    private final int ROW_SIZE = 3;
    private final int HIT_POINTS=10;

    private ImageView[] birds_img =new ImageView[COL_SIZE*ROW_SIZE];
    private Timer game_timer= new Timer();
    private GridLayout gridLayout;
    private TextView timer;
    private TextView score;
    private long mTimeLeftInMillis=30000;
    private int currentScore=0;
    private int playerLifeCounter=3;
    private int lastHoleIndex = -1;
    private String playerName;
    private boolean isWinner=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //get player name from main activity
        Intent intent = getIntent();
        playerName = intent.getStringExtra(MainActivity.EXTRA_TEXT);

        timer=findViewById(R.id.timer);
        score=findViewById(R.id.score);
        GameTimer();


        LinearLayout gameLayout =findViewById(R.id.game_linear_layout);
        gridLayout= createGridLayout(COL_SIZE,ROW_SIZE);
        gameLayout.addView(gridLayout);
        
        for (int i = 0; i <COL_SIZE*ROW_SIZE ; i++) {
            LinearLayout cell=new LinearLayout(this);
            cell.setOrientation(LinearLayout.VERTICAL);

            final ImageView bird_img =new ImageView(this);
            bird_img.setImageResource(R.drawable.blue_bird);
            bird_img.setAlpha(0f);
            birds_img[i]=bird_img;

            ImageView hole_img=new ImageView(this);
            hole_img.setImageResource(R.drawable.hole);
            hole_img.setPadding(50,70,50,70);

            cell.addView(bird_img);
            cell.addView(hole_img);

            gridLayout.addView(cell);

            bird_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(bird_img.getAlpha()>0) {
                        currentScore = currentScore + HIT_POINTS;
                        String updateScore = currentScore + "";
                        score.setText(updateScore);
                        if(currentScore>=30) {
                            isWinner=true;
                            EndGame();
                        }
                    }
                    else{
                        DecreasePlayerLife();
                    }

                }
            });



        }
        // Timer to pop up bird from his hole
        game_timer.schedule(new TimerTask() {
            public void run() {
                int randomIndex;
                do {
                   randomIndex = new Random().nextInt(COL_SIZE * ROW_SIZE - 1);
                }while(randomIndex==lastHoleIndex);

                lastHoleIndex=randomIndex;
                BirdInRandomHole(randomIndex);

            }},2000,2000);
    }

    private GridLayout createGridLayout(int col, int row) {
        GridLayout gridLayout =new GridLayout(getApplicationContext());
        gridLayout.setColumnCount(col);
        gridLayout.setRowCount(row);

        return gridLayout;

    }

    private void GameTimer(){
         new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d", seconds);
                timer.setText(timeLeftFormatted);
            }

            @Override
            public void onFinish() {
                EndGame();
            }
        }.start();

    }

    private void BirdInRandomHole(int randomIndex){

        final ImageView currentAngryBirds=birds_img[randomIndex];
        currentAngryBirds.post(new Runnable() {
            @Override
            public void run() {

                ObjectAnimator animation1 = ObjectAnimator.ofFloat(currentAngryBirds, "translationY", 100f,-20f,100f);
                animation1.setDuration(2000);
                animation1.start();

                ObjectAnimator animation2 = ObjectAnimator.ofFloat(currentAngryBirds, "alpha", 1,0);
                animation2.setDuration(2000);
                AnimatorSet animSetXY = new AnimatorSet();
                animSetXY.play(animation1).with(animation2);
                animSetXY.start();



            }
        });
    }

    private void DecreasePlayerLife(){
        switch (playerLifeCounter) {
            case 3:
                findViewById(R.id.heart_img_3).setVisibility(View.INVISIBLE);
                playerLifeCounter--;
                break;
            case 2:
                findViewById(R.id.heart_img_2).setVisibility(View.INVISIBLE);
                playerLifeCounter--;
                break;
            case 1:
                findViewById(R.id.heart_img_1).setVisibility(View.INVISIBLE);
                playerLifeCounter--;
                EndGame();
                break;
            default:
                    break;

        }

    }

    private void EndGame(){

        Intent intent = new Intent(this,GameOverActivity.class);
        intent.putExtra(EXTRA_PLAYER_NAME, playerName);
        intent.putExtra(EXTRA_PLAYER_SCORE,currentScore);
        intent.putExtra(EXTRA_PLAYER_LIFE, playerLifeCounter);
        intent.putExtra(EXTRA_PLAYER_IS_WINNER, isWinner);

        game_timer.cancel();
        startActivity(intent);
        this.finish();


    }

}
