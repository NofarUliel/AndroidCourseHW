package com.example.class3;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout mainLayout =findViewById(R.id.main_layout);
       GridLayout gridLayout= createGridLayout(3,3);
       mainLayout.addView(gridLayout);
        for (int i = 0; i <9 ; i++) {
            Button view=new Button(this);
            view.setBackgroundColor(R.drawable.color_view);
            view.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    ObjectAnimator animation1 = ObjectAnimator.ofFloat(view, "scaleY", 1f,0f,1f);
                    animation1.setDuration(1000);
                    ObjectAnimator animation2 = ObjectAnimator.ofFloat(view, "scaleX", 0f,1f,0f,1f);
                    animation2.setDuration(1000);
                    ObjectAnimator animation3 = ObjectAnimator.ofFloat(view, "rotation", 0f,360f,0f);
                    animation2.setDuration(2000);
                    AnimatorSet animSetXY = new AnimatorSet();
                    animSetXY.play(animation1).with(animation2).with(animation3);
                    animSetXY.start();
                }
            });
            gridLayout.addView(view);


        }
    }

    private GridLayout createGridLayout(int col, int row) {
        GridLayout gridLayout =new GridLayout(getApplicationContext());
        gridLayout.setColumnCount(col);
        gridLayout.setRowCount(row);
        gridLayout.setPadding(12,12,12,12);
        return gridLayout;

    }





}
