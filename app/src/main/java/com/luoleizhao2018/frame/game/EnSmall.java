package com.luoleizhao2018.frame.game;

/**
 * Created by Larry on 6/16/2015.
 */
import com.luoleizhao2018.frame.game.Enemy;
public class EnSmall extends Enemy{
    EnSmall(double x, double y) {
        super(x, y);
        health = 30;
        maxSpeed = 16;
        radius = 14;
        damage = 20;
        withinRadius = 1;
        fireFrequency = 100;
        value = 10;
        fireTime = (int)(fireFrequency);
        image = Assets.enSmall;
    }
}
