package com.example.whackanangrybirds;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
    private final int COL_SIZE = 3;
    private final int ROW_SIZE = 3;
    private ImageView[] birds_img =new ImageView[COL_SIZE*ROW_SIZE];
    private GridLayout gridLayout;
    private CountDownTimer mCountDownTimer;
    private TextView timer;
    private long mTimeLeftInMillis=30000;
    private Timer pop_bird_timer=new Timer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        timer=findViewById(R.id.timer);
        GameTimer();

       LinearLayout gameLayout =findViewById(R.id.game_linear_layout);
        gridLayout= createGridLayout(COL_SIZE,ROW_SIZE);
        gameLayout.addView(gridLayout);
        for (int i = 0; i <COL_SIZE*ROW_SIZE ; i++) {
            LinearLayout cell=new LinearLayout(this);
            cell.setOrientation(LinearLayout.VERTICAL);

            ImageView bird_img =new ImageView(this);
            bird_img.setImageResource(R.drawable.blue_bird);
            bird_img.setVisibility(View.INVISIBLE);
            birds_img[i]=bird_img;

            ImageView hole_img=new ImageView(this);
            hole_img.setId(20+i);
            hole_img.setImageResource(R.drawable.hole);
            hole_img.setScaleX(1.15f);
            hole_img.setPadding(50,70,50,70);


            cell.addView(bird_img);
            cell.addView(hole_img);

            gridLayout.addView(cell);

            // Timer to pop our mole back down if player fails to hit it

            pop_bird_timer.schedule(new TimerTask() {
                public void run() {
                    BirdInRandomHole();

                }},2000);


        }
    }

    private GridLayout createGridLayout(int col, int row) {
        GridLayout gridLayout =new GridLayout(getApplicationContext());
        gridLayout.setColumnCount(col);
        gridLayout.setRowCount(row);


        return gridLayout;

    }

    private void GameTimer(){
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d", seconds);

                timer.setText(timeLeftFormatted);
            }

            @Override
            public void onFinish() {


            }
        }.start();

    }



   private void BirdInRandomHole(){
        int random = new Random().nextInt(COL_SIZE*ROW_SIZE-1);
        ImageView currentAngryBirds = birds_img[random];
        currentAngryBirds.setVisibility(View.VISIBLE);
        ObjectAnimator animation1 = ObjectAnimator.ofFloat(currentAngryBirds, "translationY", 100f,-20f,100f);
        animation1.setDuration(2000);
        animation1.start();



}




}
