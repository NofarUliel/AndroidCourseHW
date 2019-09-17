package com.example.hw2;

import java.io.Serializable;

public class Player implements Serializable {

    private String name;
    private int score;
    private int life;
    private double location_x;
    private double location_y;
    private boolean isWinner;

    public Player(String name){
        this.name=name;
        this.score=0;
        this.life=0;
        this.location_x=0.0;
        this.location_x=0.0;
    }
    public Player(String name,int score,double location_x,double location_y){
        this.name=name;
        this.score=score;
        this.location_x=location_x;
        this.location_y=location_y;
    }

    public void setScore(int score){
        this.score=score;
    }
    public void setLife(int life){
        this.life=life;
    }
    public void setLocation_x(double location_x){
        this.location_x=location_x;
    }
    public void setLocation_y(double location_y){
        this.location_y=location_y;
    }
    public void setWinner(boolean isWinner){
        this.isWinner=isWinner;
    }
    public String getPlayerName(){return this.name;}
    public int getScore(){return this.score;}
    public int getLife(){return this.life;}
    public double getLocation_x(){return this.location_x;}
    public double getLocation_y(){return this.location_y;}
    public boolean getIsWinner(){return this.isWinner;}

    public String toString(){
        return "( "+this.name +
                ","+score +")";
    }
}
