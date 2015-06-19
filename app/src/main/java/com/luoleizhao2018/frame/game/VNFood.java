package com.luoleizhao2018.frame.game;

/**
 * Created by Larry on 6/17/2015.
 */
public class VNFood extends Venue {
    VNFood(double x, double y) {
        super(x, y);
        cooldown = 120;
        image = Assets.vnFood;
        price = 30;
    }

    @Override
    public void onInteract(Player plyr) {
        plyr.money -= price;
        plyr.health = Math.min(plyr.max_health,plyr.health+ 30);
    }
}
