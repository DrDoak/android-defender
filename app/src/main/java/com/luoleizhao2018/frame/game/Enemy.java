package com.luoleizhao2018.frame.game;

/**
 * Created by Larry on 6/15/2015.
 */

import com.luoleizhao2018.frame.game.Unit;

public class Enemy extends Unit {
    protected int withinRadius = 1;
    public int damage = 10;
    protected Entity myTarget;
    public boolean toFire = false;
    public int value = 10;
    Enemy(double x, double y) {
        super(x, y);
    }

    @Override
    public void update(){
        super.update();
        moveToPoint(myTarget.x, myTarget.y, withinRadius);
        if (distanceToPoint(myTarget.x, myTarget.y) < withinRadius && fireTime <=0)
            toFire = true;
    }

    public void setTarget(Entity target) {
        myTarget = target;
    }
}
