package com.luoleizhao2018.frame.game;

/**
 * Created by Larry on 6/16/2015.
 */
import com.luoleizhao2018.frame.game.Entity;
public abstract class Venue extends Entity{
    public int cooldown;
    public int timer;
    public boolean ready;
    public int price = 0;

    Venue(double x, double y) {
        super(x, y);
        timer = cooldown;
        radius = 16;
    }

    public abstract void onInteract( Player plyr);

    @Override
    public void update(){
        super.update();
        if (timer >= 0) {
            ready = false;
            timer--;
        } else {
            ready = true;
        }
    }

}
