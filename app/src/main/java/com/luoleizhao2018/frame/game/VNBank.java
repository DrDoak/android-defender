package com.luoleizhao2018.frame.game;

/**
 * Created by Larry on 6/17/2015.
 */
public class VNBank extends Venue {
    VNBank(double x, double y) {
        super(x, y);
        cooldown = 250;
        image = Assets.vnBank;
        price = 0;
    }

    @Override
    public void onInteract(Player plyr) {
        plyr.money += 100;
    }
}
