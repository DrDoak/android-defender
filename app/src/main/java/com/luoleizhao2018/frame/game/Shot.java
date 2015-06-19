package com.luoleizhao2018.frame.game;

/**
 * Created by Larry on 6/15/2015.
 */
import com.luoleizhao2018.frame.game.Entity;

import java.util.List;

public abstract class Shot extends Entity{
    protected int damage = 0;
    Shot(double x2,double y2, double targetX, double targetY) {
        super(x2,y2);
    }
    public void initializeSpeed(double targetX, double targetY) {
        double angle = angleToPoint(targetX, targetY);
        velX = maxSpeed * Math.cos(angle);
        velY = maxSpeed * Math.sin(angle);
    }

//    @Override
//    public void update() {
//        x = x + velX;
//        y = y + velY;
//    }
}
