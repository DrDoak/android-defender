package com.luoleizhao2018.frame.game;

/**
 * Created by Larry on 6/16/2015.
 */
public class SHEvil extends Shot {
    SHEvil(double x2, double y2, double targetX, double targetY) {
        super(x2, y2, targetX, targetY);
        damage = 10;
        radius = 10;
        maxSpeed = 20;
        initializeSpeed(targetX,targetY);
        image = Assets.shEvil;
    }
}
