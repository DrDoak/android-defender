package com.luoleizhao2018.frame.game;

/**
 * Created by Larry on 6/16/2015.
 */
import com.luoleizhao2018.frame.game.Shot;
public class SHStandard extends Shot{

    SHStandard(double x2, double y2, double targetX, double targetY) {
        super(x2, y2, targetX, targetY);
        damage = 40;
        maxSpeed = 20;
        radius = 10;
        initializeSpeed(targetX,targetY);
        image = Assets.shBasic;
    }

    public void setDamage(int dmg) {
        damage = dmg;
    }
}
