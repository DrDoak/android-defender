package com.luoleizhao2018.frame.game;

/**
 * Created by Larry on 6/17/2015.
 */
public class VNAirport extends Venue {
    VNAirport(double x, double y) {
        super(x, y);
        cooldown = 1200;
        price = 200;
        image = Assets.vnAirport;
    }

    @Override
    public void onInteract(Player plyr) {
        //plyr.lives ++;
    }
}
