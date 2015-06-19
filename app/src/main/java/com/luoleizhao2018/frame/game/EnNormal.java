package com.luoleizhao2018.frame.game;

/**
 * Created by Larry on 6/16/2015.
 */
import com.luoleizhao2018.frame.game.Enemy;
public class EnNormal extends Enemy{
    EnNormal(double x, double y) {
        super(x, y);
        health = 100;
        maxSpeed = 5;
        radius = 14;
        damage = 30;
        withinRadius = 250;
        fireFrequency = 30;
        value = 25;
        fireTime = (int)(fireFrequency);
        image = Assets.enMid;
    }
}
