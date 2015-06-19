package com.luoleizhao2018.frame.game;

/**
 * Created by Larry on 6/17/2015.
 */
public class VNLibrary extends Venue {
    VNLibrary(double x, double y) {
        super(x, y);
        cooldown = 100;
        image = Assets.vnLibrary;
    }

    @Override
    public void onInteract(Player plyr) {
        int rand = 1 + (int)(Math.random()*4);
        if (rand == 1)
            plyr.setPowerUp("power");
        else if (rand == 2)
            plyr.setPowerUp("range");
        else if (rand == 3)
            plyr.setPowerUp("speed");
        else
            plyr.setPowerUp("intelligence");
    }
}
