package com.unorthwestern.luoleizhao2018.defender;

import android.os.Bundle;

import com.luoleizhao2018.frame.Screen;
import com.luoleizhao2018.frame.game.GameScreen;
import com.luoleizhao2018.frame.implementation.AndroidGame;

public class MainGame extends AndroidGame {

    public double[] xCoordinates = new double[25];
    public double[] yCoordinates = new double[25];
    public String[] types = new String[25];
    public String backgroundPath = "";

    @Override
    public Screen getInitScreen() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            backgroundPath = extras.getString("backgroundPath");
            xCoordinates = extras.getDoubleArray("xCoordinates");
            yCoordinates = extras.getDoubleArray("yCoordinates");
            types = extras.getStringArray("types");
        }
        return new GameScreen(this);
    }
}