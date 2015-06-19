package com.luoleizhao2018.frame.game;

import com.luoleizhao2018.frame.Image;

/**
 * Created by Larry on 6/15/2015.
 */
public abstract class Entity {
    protected double x;
    protected double y;
    protected double maxSpeed;
    protected double velX;
    protected double velY;
    protected double radius;
    public Image image;

    Entity(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distanceToPoint(double x2, double y2) {
        return Math.sqrt((x2 - x) * (x2 - x) + (y2 - y) * (y2 - y));
    }

    public double angleToPoint(double x2, double y2) {
        return Math.atan2(y2 - y, x2 - x);
    }

    public void update() {
        x = x + velX;
        y = y + velY;
    }

    public void moveToPoint(double x2, double y2, double proximity) {
        if (distanceToPoint(x2, y2) < proximity) {
            velX = 0;
            velY = 0;
            return;
        } else {
            double angle = angleToPoint(x2, y2);
            velX = maxSpeed * Math.cos(angle);
            velY = maxSpeed * Math.sin(angle);
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }

    public boolean checkCollide(Entity other) {
        double dist = distanceToPoint(other.getX(),other.getY());
        if (dist < radius + other.getRadius())
            return true;
        else
            return false;
    }

    public void draw() {}

}
