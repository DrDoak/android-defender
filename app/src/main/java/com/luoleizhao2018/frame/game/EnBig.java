package com.luoleizhao2018.frame.game;

/**
 * Created by Larry on 6/16/2015.
 */
import com.luoleizhao2018.frame.game.Enemy;
public class EnBig extends Enemy{
    EnBig(double x, double y) {
        super(x, y);
        health = 500;
        maxSpeed = 4;
        radius = 25;
        damage = 50;
        withinRadius = 350;
        fireFrequency = 15;
        value = 70;
        fireTime = (int)(fireFrequency);
        image = Assets.enHeavy;
    }
}