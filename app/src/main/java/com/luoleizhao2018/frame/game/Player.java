package com.luoleizhao2018.frame.game;

import com.luoleizhao2018.frame.Graphics;
import com.luoleizhao2018.frame.Input;
import com.luoleizhao2018.frame.game.Unit;

import java.util.List;

/**
 * Created by Larry on 6/15/2015.
 */
public class Player extends Unit{
    
    private boolean isMoving = false;
    public int money;
    public double statSpeed;
    public double statPower;
    public double statRate;
    public double statRange;
    public double statInt;
    public double statVit;
    private final double INIT_SPEED = 10;
    private final double INIT_FREQ = 10;
    private final int INIT_RANGE = 250;
    private final double INIT_POWER = 1.0;
    private final int INIT_VIT = 50;
    public String currentPower;
    private int powerUpTimer;
    private int intelligence;

    Player(double x, double y) {
        super(x, y);
        initializeVariables();
    }

    public void initializeVariables() {
        radius = 16;
        fireTime = 0;
        statPower = 0;
        statRate = 0;
        statRange = 0;
        statInt = 0;
        statVit = 0;
        intelligence = 0;
        upgradeStat("nothing");
        money = 0;
        health = max_health;
        currentPower = "None";
    }
    @Override
    public void update(){
        velX = 0;
        velY = 0;
        if (isMoving)
            moveToPoint(targetX,targetY,4);
        super.update();
        if (powerUpTimer > 0)
            powerUpTimer --;
        else if (powerUpTimer == 0) {
            setPowerUp("nothing");
            powerUpTimer --;
        }
    }
    public void upgradeStat(String stat) {
        if (stat == "speed") {
            statSpeed = Math.min(30,statSpeed + 1 + (intelligence * 0.1));
        } else if (stat == "firerate") {
            statRate = Math.min(30,statRate + 1 + (intelligence * 0.1));
        } else if (stat == "power") {
            statPower = Math.min(30,statPower + 1 + (intelligence * 0.1));
        } else if (stat == "range") {
            statRange = Math.min(30,statRange + 1 + (intelligence * 0.1));
        } else if (stat == "intelligence") {
            statInt = Math.min(30,intelligence + 1);
        } else if (stat == "vitality") {
            statVit = Math.min(400,statVit + 1 + intelligence);
        }
        range = (int)(INIT_RANGE + (statRange*10));
        maxSpeed = (int)(INIT_SPEED + (statSpeed));
        fireFrequency = (int)(INIT_FREQ - (0.2 * statRate));
        power = (INIT_POWER + statPower);
        intelligence = (int)statInt;
        max_health = (int)(INIT_VIT + statVit);
        if (currentPower == "intelligence")
            powerUpTimer = 0;
    }

    public void setPowerUp(String powerUp) {
        upgradeStat("nothing"); //reset Stats
        if (powerUp.equals("range")) {
            range = 1000;
            currentPower = "Crazy Range";
        } else if (powerUp.equals("intelligence")) {
            intelligence = 30;
            currentPower = "Ultra Learn";
        } else if (powerUp.equals("speed")) {
            maxSpeed = 30;
            currentPower = "Ludicrous Speed";
        } else if (powerUp.equals(power)) {
            power = 8;
            currentPower = "Overwhelming Power";
        } else {
            currentPower = "None";
        }
        powerUpTimer = 200;
    }
    public void updatePosition(List<Input.TouchEvent> touchEvents) {
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            Input.TouchEvent event = touchEvents.get(i);
            if (event.type == Input.TouchEvent.TOUCH_DOWN || event.type == Input.TouchEvent.TOUCH_HOLD ||
                    event.type == Input.TouchEvent.TOUCH_DRAGGED) {
                targetX = event.x;
                targetY = event.y;
                isMoving = true;
            }

            if (event.type == Input.TouchEvent.TOUCH_UP) {
                targetX = x;
                targetY = y;
                isMoving = false;
            }
        }
    }
}
