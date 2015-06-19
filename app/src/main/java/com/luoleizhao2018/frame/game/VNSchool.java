package com.luoleizhao2018.frame.game;

/**
 * Created by Larry on 6/17/2015.
 */
public class VNSchool extends Venue {
    VNSchool(double x, double y) {
        super(x, y);
        cooldown = 100;
        price = 100;
        image = Assets.vnSchool;
    }

    @Override
    public void onInteract(Player plyr) {
        plyr.upgradeStat("intelligence");
    }
}
