package com.example.hw2;


import androidx.appcompat.app.AppCompatActivity;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class GameActivity extends AppCompatActivity {
    public static final String EXTRA_PLAYER= "GameActivity.EXTRA_PLAYER";
    private final int COL_SIZE = 3;
    private final int ROW_SIZE = 3;
    private final int HIT_POINTS=1;
    private final int HIT_BOMB_POINTS=3;
    private final int BOMB_INDEX=0;


    private RelativeLayout[] cellsArr =new RelativeLayout[COL_SIZE*ROW_SIZE];
    private int[] myImageList = new int[]{R.drawable.bomb, R.drawable.pig,R.drawable.red,R.drawable.old_pig,R.drawable.blue,R.drawable.yellow};
    private Timer game_timer= new Timer();
    private CountDownTimer timer_down;
    private GridLayout gridLayout;
    private TextView timer;
    private TextView score;
    private long mTimeLeftInMillis=30000;
    private int currentScore=0;
    private int playerLifeCounter=3;
    private int lastHoleIndex = -1;
    private int lastImageIndex = -1;
    private String playerName;
    private boolean isWinner=false;
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        GetIntent();         //get player name and player location from MaimActivity

        timer=findViewById(R.id.timer);
        score=findViewById(R.id.score);

        GameTimer();         //start timer of game (30 second)
        buildGridLayout();


        // Timer to pop up bird from his box
        game_timer.schedule(new TimerTask() {
            public void run() {
                BirdInRandomBox();
            }},2000,1000);
    }


    //get player name and player location from MaimActivity
    private void GetIntent() {

        Intent intent = getIntent();
        playerName = intent.getStringExtra(GameOverActivity.EXTRA_PLAYER_NAME);
        if(playerName==null){
            player= (Player) intent.getSerializableExtra(MainActivity.EXTRA_PLAYER);
            playerName=player.getPlayerName();
        }

    }

    //start timer of game (30 second)
    private void GameTimer(){
        timer_down= new CountDownTimer(mTimeLeftInMillis, 1000) {
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

    @SuppressLint("WrongConstant")
    private void buildGridLayout(){

        //set size of grid layout
        gridLayout=findViewById(R.id.grid_layout);
        gridLayout.setColumnCount(COL_SIZE);
        gridLayout.setRowCount(ROW_SIZE);

        for (int i = 0; i <COL_SIZE*ROW_SIZE ; i++) {

            RelativeLayout cell=new RelativeLayout(this);
            RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            cell.setLayoutParams(params);


            final ImageView bird_img =new ImageView(this);
            bird_img.setImageResource(R.drawable.pig);
            bird_img.setTag(lastImageIndex);
            bird_img.setAlpha(0f);
            bird_img.setId(i);

            bird_img.setLayoutParams(params);

            final TextView increasePoint=new TextView(this);
            increasePoint.setText("+1");
            increasePoint.setTextColor(Color.parseColor("#76e25e"));
            increasePoint.setAlpha(0f);
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            params.addRule(RelativeLayout.RIGHT_OF,i);
            params.addRule(RelativeLayout.ABOVE,(100*i));
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            increasePoint.setLayoutParams(params);

            final TextView decreasePoint=new TextView(this);
            decreasePoint.setText("-3");
            decreasePoint.setTextColor(Color.parseColor("#FF4162"));
            decreasePoint.setAlpha(0f);
            decreasePoint.setLayoutParams(params);


            ImageView box_img=new ImageView(this);
            box_img.setImageResource(R.drawable.box);
            box_img.setId(100*i);

            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
           params.addRule(RelativeLayout.BELOW,i);
            box_img.setLayoutParams(params);


            cell.addView(bird_img);
            cell.addView(increasePoint);
            cell.addView(decreasePoint);
            cell.addView(box_img);
            gridLayout.addView(cell);
            cellsArr[i]=cell;

            bird_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(bird_img.getAlpha()>0) {
                        //hit bomb
                        if((int) (bird_img.getTag())==BOMB_INDEX)
                        {
                            currentScore = currentScore - HIT_BOMB_POINTS;

                            ObjectAnimator animation1 = ObjectAnimator.ofFloat(bird_img, "scaleY",1.5f,0);
                            animation1.setDuration(1000);

                            ObjectAnimator animation2 = ObjectAnimator.ofFloat(bird_img, "scaleX",1.5f,0);
                            animation2.setDuration(1000);

                            ObjectAnimator animation3 = ObjectAnimator.ofFloat(decreasePoint, "alpha",1,0);
                            animation2.setDuration(1000);

                            AnimatorSet animSetXY = new AnimatorSet();
                            animSetXY.play(animation1).with(animation2).with(animation3);
                            animSetXY.start();


                        }
                        else {
                            currentScore = currentScore + HIT_POINTS;

                            ObjectAnimator animation1 = ObjectAnimator.ofFloat(bird_img, "rotation", 0,360);
                            animation1.setDuration(1000);
                            ObjectAnimator animation2 = ObjectAnimator.ofFloat(increasePoint, "alpha",1,0);
                            animation2.setDuration(1000);

                            AnimatorSet animSetXY = new AnimatorSet();
                            animSetXY.play(animation1).with(animation2);
                            animSetXY.start();

                        }
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



        }

    //pop up random bird in random box
    private void BirdInRandomBox(){

        lastHoleIndex =  RandomIndex(COL_SIZE * ROW_SIZE ,lastHoleIndex);
        final RelativeLayout cell_in_grid = cellsArr[lastHoleIndex];
        lastImageIndex = RandomIndex(myImageList.length,lastImageIndex);
        final ImageView currentAngryBirds = (ImageView) cell_in_grid.getChildAt(0);


        currentAngryBirds.post(new Runnable() {
            @Override
            public void run() {
                currentAngryBirds.setImageResource(myImageList[lastImageIndex]);
                currentAngryBirds.setTag(lastImageIndex);

                ObjectAnimator animation1 = ObjectAnimator.ofFloat(currentAngryBirds, "translationY", 100f,-20f,100f);
                animation1.setDuration(1000);

                ObjectAnimator animation2 = ObjectAnimator.ofFloat(currentAngryBirds, "alpha", 1,0);
                animation2.setDuration(1000);
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

    //get random index between 0 to maxValue
    private int RandomIndex(int maxValue,int lastIndex){
        int randomIndex;
        do {
            randomIndex = new Random().nextInt(maxValue);
        }while(randomIndex==lastIndex);
        return randomIndex;
    }
    //pass data to GameOverActivity
    private void EndGame(){

        Intent intent = new Intent(this,GameOverActivity.class);
        player.setScore(currentScore);
        player.setLife(playerLifeCounter);
        player.setWinner(isWinner);
        intent.putExtra(EXTRA_PLAYER,player);

        Log.d("end game", "EndGame: start");
        game_timer.cancel();
        timer_down.cancel();
        startActivity(intent);
        this.finish();


    }

//    private int getScreenHeight(Context context) {
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        return displayMetrics.heightPixels;
//    }
//    private int getScreenWidth(Context context) {
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        return displayMetrics.widthPixels;
//    }

}
