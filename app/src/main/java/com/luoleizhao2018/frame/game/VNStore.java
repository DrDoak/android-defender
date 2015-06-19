package com.luoleizhao2018.frame.game;

/**
 * Created by Larry on 6/17/2015.
 */
public class VNStore extends Venue {
    VNStore(double x, double y) {
        super(x, y);
        cooldown = 200;
        price = 150;
        image = Assets.vnStore;
    }

    @Override
    public void onInteract(Player plyr) {
        int rand = 1 + (int)(Math.random()*5);
        if (rand == 1)
            plyr.upgradeStat("power");
        else if (rand == 2)
            plyr.upgradeStat("range");
        else if (rand == 3)
            plyr.upgradeStat("speed");
        else if (rand == 4)
            plyr.upgradeStat("firerate");
        else
            plyr.upgradeStat("vitality");
    }
}
