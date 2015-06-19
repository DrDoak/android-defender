package com.luoleizhao2018.frame.game;

import com.luoleizhao2018.frame.game.Entity;
/**
 * Created by Larry on 6/15/2015.
 */
public abstract class Unit extends Entity{
    protected int health;
    protected int max_health;
    protected double targetX = 0;
    protected double targetY = 0;
    public int range;
    public double power;
    public double fireFrequency;
    public int fireTime;

    Unit(double x, double y) {
        super(x, y);
        health = 100;
        max_health = 100;
    }

    @Override
    public void update(){
        super.update();
        if (fireTime > 0)
            fireTime --;
    }

    public void takeDamage(double damage) {
        health -= damage;
    }
    public void die() {

    }
}
