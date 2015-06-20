package com.luoleizhao2018.frame.game;


import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;

import com.luoleizhao2018.frame.Game;
import com.luoleizhao2018.frame.Graphics;
import com.luoleizhao2018.frame.Image;
import com.luoleizhao2018.frame.Screen;
import com.luoleizhao2018.frame.Input.TouchEvent;
import com.luoleizhao2018.frame.game.Player;
import com.luoleizhao2018.frame.implementation.AndroidImage;
import com.unorthwestern.luoleizhao2018.defender.MainGame;
import com.unorthwestern.luoleizhao2018.defender.MyApplication;

public class GameScreen extends Screen {
    enum GameState {
        Ready, Running, Paused, GameOver
    }
    public Player currentPlayer;
    public List<Enemy> enemyList = new ArrayList<Enemy>();
    public List<Shot> shotList = new ArrayList<Shot>();
    public List<Shot> shotListEn = new ArrayList<Shot>();
    public List<Venue> venueList = new ArrayList<Venue>();
    public final int MAX_ENEMIES_HARD = 15;
    public final int MAX_SPAWN_TIME = 8;
    public final int MIN_SPAWN_TIME = 5;
    public final int MAX_SHOTS = 10;
    int max_enemies;
    int min_spawn;
    int max_spawn;
    int heavy_proportion;
    int mid_proportion;
    private int spawnTime;
    GameState state = GameState.Ready;
    int ticks = 0;
    int livesLeft = 3;
    Paint paint;

    public GameScreen(Game game) {
        super(game);

        // Initialize game variables
        initializeGame(game);
        // Defining a paint object
        paint = new Paint();
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
    }

    private void initializeGame(Game game) {
        Graphics g = game.getGraphics();
        //initialize game resource
        //Assets.background = g.newImage("map_background.png",Graphics.ImageFormat.RGB565);
        String path = ((MainGame)game).backgroundPath;
        Bitmap bitmap1 = BitmapFactory.decodeFile(path);
        Display display = ((MainGame)game).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap1, size.x,
                size.y, false);
        AndroidImage bkground = new AndroidImage(newBitmap, Graphics.ImageFormat.RGB565);
        Assets.player = g.newImage("player_basic.png", Graphics.ImageFormat.RGB565);
        Assets.background = bkground;
        Assets.enMid = g.newImage("enemy_mid.png", Graphics.ImageFormat.RGB565);
        Assets.enHeavy = g.newImage("enemy_heavy.png", Graphics.ImageFormat.RGB565);
        Assets.enSmall = g.newImage("enemy_small.png", Graphics.ImageFormat.RGB565);
//        Assets.enBig = g.newImage("enemy_heavy.jpg", Graphics.ImageFormat.RGB565);
        Assets.shBasic = g.newImage("bullet_basic.png", Graphics.ImageFormat.RGB565);
        Assets.shEvil = g.newImage("bullet_evil.png", Graphics.ImageFormat.RGB565);
        Assets.ready = g.newImage("ready.png", Graphics.ImageFormat.RGB565);
        Assets.vnAirport = g.newImage("venue_airport.png",Graphics.ImageFormat.RGB565);
        Assets.vnBank = g.newImage("venue_bank.png",Graphics.ImageFormat.RGB565);
        Assets.vnFood = g.newImage("venue_food.png",Graphics.ImageFormat.RGB565);
        Assets.vnSchool = g.newImage("venue_school.png",Graphics.ImageFormat.RGB565);
        Assets.vnStore = g.newImage("venue_store.png",Graphics.ImageFormat.RGB565);
        Assets.vnLibrary = g.newImage("venue_library.png",Graphics.ImageFormat.RGB565);
        currentPlayer = new Player(640,480);
        spawnTime = MIN_SPAWN_TIME + (int)(Math.random()*MAX_SPAWN_TIME);
        //initialize game variables
        max_enemies = 5;
        heavy_proportion = 95;
        mid_proportion = 70;
        ticks = 0;
        initializeVenues(game);
    }

    public void initializeVenues(Game game) {

        String[] typelist = ((MainGame)game).types;
        double[] xCoord = ((MainGame)game).xCoordinates;
        double[] yCoord = ((MainGame)game).yCoordinates;
        for(int i = 0;i < typelist.length;i++){
            if (xCoord[i] != 0) {
                double newX = xCoord[i];
                double newY = yCoord[i];
                String type = typelist[i];
                Venue newVen;
                if (type.equals("library"))
                    newVen = new VNLibrary(newX, newY);
                else if (type.equals("airport"))
                    newVen = new VNAirport(newX, newY);
                else if (type.equals("store"))
                    newVen = new VNStore(newX, newY);
                else if (type.equals("school"))
                    newVen = new VNSchool(newX, newY);
                else if (type.equals("bank"))
                    newVen = new VNBank(newX, newY);
                else
                    newVen = new VNFood(newX, newY);
                venueList.add(newVen);
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        if (state == GameState.Ready)
            updateReady(touchEvents);
        if (state == GameState.Running)
            updateRunning(touchEvents, deltaTime);
        if (state == GameState.Paused)
            updatePaused(touchEvents);
        if (state == GameState.GameOver)
            updateGameOver(touchEvents);
    }

    private void updateReady(List<TouchEvent> touchEvents) {

        // This example starts with a "Ready" screen.
        // When the user touches the screen, the game begins.
        // state now becomes GameState.Running.
        // Now the updateRunning() method will be called!

        if (touchEvents.size() > 0)
            state = GameState.Running;
    }

    private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {
        //Advance Global game variables
        ticks ++;
        spawnTime --;
        adjustDifficulty();
        if (spawnTime <= 0)
            spawnEnemy();

        // 3. Call individual update() methods here.
        //update player
        currentPlayer.updatePosition(touchEvents);
        currentPlayer.update();
        Enemy target = null;
        double minimumDist = 9000;
        if (currentPlayer.health <= 0) {
            if (livesLeft > 0) {
                livesLeft--;
                currentPlayer.initializeVariables();
                currentPlayer.x = 640;
                currentPlayer.y = 480;
            } else {
                state = GameState.GameOver;
            }
        }
        for (int j = 0; j < enemyList.toArray().length;j++) {
            double dist = currentPlayer.distanceToPoint(enemyList.get(j).x, enemyList.get(j).y);
            if (dist < currentPlayer.range)
                if (dist < minimumDist) {
                    minimumDist = dist;
                    target = enemyList.get(j);
                }
        }
        if (minimumDist != 9000 && currentPlayer.fireTime <= 0 && target != null) {
            SHStandard newShot = new SHStandard(currentPlayer.x, currentPlayer.y,target.x, target.y);
            double remainder = currentPlayer.fireFrequency - (int)(currentPlayer.fireFrequency);
            double rand = Math.random();
            if (rand < remainder)
                currentPlayer.fireTime = (int)(currentPlayer.fireFrequency) + 1;
            else
                currentPlayer.fireTime = (int)(currentPlayer.fireFrequency);
            shotList.add(newShot);
        }

        //update All Player Shots
        for (int i = 0; i< shotList.toArray().length;i++){
            Shot temp = shotList.get(i);
            temp.update();
            if (temp.x < 0 || temp.x > 1280 || temp.y < 0 || temp.y > 960)
                shotList.remove(i);
            else
                for (int j = 0; j < enemyList.toArray().length;j++)
                    if (temp.checkCollide(enemyList.get(j))) {
                        enemyList.get(j).takeDamage(temp.damage * currentPlayer.power);
                        shotList.remove(i);
                        break;
                    }
        }
        //update All Enemy Shots
        for (int i = 0; i< shotListEn.toArray().length;i++){
            Shot temp = shotListEn.get(i);
            temp.update();
            if (temp.x < 0 || temp.x > 1280 || temp.y < 0 || temp.y > 960)
                shotListEn.remove(i);
            else if (temp.checkCollide(currentPlayer)) {
                currentPlayer.takeDamage(temp.damage);
                shotListEn.remove(i);
                break;
            }
        }
        //update All enemies
        for (int i = 0; i< enemyList.toArray().length;i++){
           Enemy temp =  enemyList.get(i);
           temp.update();
           if (temp.checkCollide(currentPlayer)) {
               currentPlayer.takeDamage(temp.damage);
               enemyList.remove(i);
           }
           if (temp.toFire) {
               SHEvil newShot = new SHEvil(temp.x, temp.y,currentPlayer.x, currentPlayer.y);
               temp.fireTime = (int)temp.fireFrequency;
               temp.toFire = false;
               shotListEn.add(newShot);
           }
           if (temp.health <= 0) {
               currentPlayer.money += temp.value;
               enemyList.remove(i);
           }
        }
        //Update Venues
        for (int i = 0; i< venueList.toArray().length;i++){
            Venue temp = venueList.get(i);
            temp.update();
            if (temp.checkCollide(currentPlayer) && temp.ready) {
                temp.onInteract(currentPlayer);
                temp.timer = temp.cooldown;
                temp.ready = false;
            }
        }
    }

    private void updatePaused(List<TouchEvent> touchEvents) {
        if (touchEvents.size() > 0)
            state = GameState.Running;
    }

    private void updateGameOver(List<TouchEvent> touchEvents) {
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if (event.x > 300 && event.x < 980 && event.y > 100
                        && event.y < 500) {
                    nullify();
                    game.setScreen(new GameScreen(game));
                    return;
                }
            }
        }

    }

    @Override
    public void paint(float deltaTime) {
        Graphics g = game.getGraphics();
        g.drawARGB(255,0,0,0);
        // First draw the game elements.

        // Draw background
        g.drawImage(Assets.background,0, 0);

        //draw Player
        g.drawImage(Assets.player, ((int)currentPlayer.x),((int)currentPlayer.y));
        //draw all enemies
        for (int i = 0; i< enemyList.toArray().length;i++){
            g.drawImage(enemyList.get(i).image,((int)enemyList.get(i).x),((int)enemyList.get(i).y));
        }
        //draw all stores
        for (int i = 0; i< venueList.toArray().length;i++){
            g.drawImage(venueList.get(i).image,((int)venueList.get(i).x),((int)venueList.get(i).y));
            if (venueList.get(i).ready)
                if (venueList.get(i).price != 0)
                    g.drawImage(Assets.ready,((int)venueList.get(i).x),((int)venueList.get(i).y) + 16);
                else
                    g.drawString(Integer.toString(venueList.get(i).price),
                            ((int)venueList.get(i).x) ,((int)venueList.get(i).y) + 24, paint);
        }
        //draw all shots
        for (int i = 0; i< shotList.toArray().length;i++){
            g.drawImage(shotList.get(i).image,((int)shotList.get(i).x),((int)shotList.get(i).y));
        }
        for (int i = 0; i< shotListEn.toArray().length;i++){
            g.drawImage(shotListEn.get(i).image,((int)shotListEn.get(i).x),((int)shotListEn.get(i).y));
        }
        // Secondly, draw the UI above the game elements.
        if (state == GameState.Ready)
            drawReadyUI();
        if (state == GameState.Running)
            drawRunningUI();
        if (state == GameState.Paused)
            drawPausedUI();
        if (state == GameState.GameOver)
            drawGameOverUI();
    }

    private void nullify() {
        // Set all variables to null. You will be recreating them in the
        // constructor.
        paint = null;
        // Call garbage collector to clean up memory.
        System.gc();
    }

    private void drawReadyUI() {
        Graphics g = game.getGraphics();
        g.drawARGB(155, 0, 0, 0);
        g.drawString("Tap a point on the screen to move in that direction.",
                640, 300, paint);
    }

    private void drawRunningUI() {
        Graphics g = game.getGraphics();
        g.drawARGB(10, 0, 0, 0);
        g.drawString("Current Health: " + Integer.toString(currentPlayer.health) + "/" + Integer.toString(currentPlayer.max_health),
                300, 20, paint);
        g.drawString("Points: " + Integer.toString(currentPlayer.money),
                700, 100, paint);
        g.drawString("Lives Left: " + livesLeft,
                700, 50, paint);
        g.drawString("Pwr: " + Double.toString((int)currentPlayer.statPower) +
                "  Rng: " + Double.toString((int)currentPlayer.statRange) +
                "  Spd: " + Double.toString((int)currentPlayer.statSpeed) +
                "  Int: " + Double.toString((int)currentPlayer.statInt) +
                "  Rof: " + Double.toString((int)currentPlayer.statRate),
                330, 60, paint);
        g.drawString("Current Power Up: " + currentPlayer.currentPower,
                300, 100, paint);
    }

    private void drawPausedUI() {
        Graphics g = game.getGraphics();
        // Darken the entire screen so you can display the Paused screen.
        g.drawARGB(155, 0, 0, 0);

    }

    private void drawGameOverUI() {
        Graphics g = game.getGraphics();
        g.drawRect(0, 0, 1281, 801, Color.BLACK);
        g.drawString("GAME OVER.", 640, 300, paint);
        g.drawString("Tap to Restart", 640, 500, paint);
    }

    @Override
    public void pause() {
        if (state == GameState.Running)
            state = GameState.Paused;
    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void backButton() {
        pause();
    }

    public void spawnEnemy() {
        spawnTime = min_spawn + (int)(Math.random()*max_spawn);
        if (enemyList.toArray().length < max_enemies) {
            int x = 0 + (int)(Math.random()*1280);
            int y = 0 + (int)(Math.random()*960);
            int newX = 0;
            int newY = 0;
            int side = 1 + (int)(Math.random()*4);
            if (side == 1) {
                newX = 1;
                newY = y;
            } else if (side == 2){
                newX = 1280;
                newY = y;
            } else if (side == 3) {
                newX = x;
                newY = 0;
            } else {
                newX = x;
                newY = 960;
            }
            int enemyType = 1 + (int)(Math.random()*100);
            Enemy newEnemy;
            if (enemyType > heavy_proportion)
                newEnemy = new EnBig(newX,newY);
            else if (enemyType > mid_proportion)
                newEnemy = new EnNormal(newX,newY);
            else
                newEnemy = new EnSmall(newX,newY);
            enemyList.add(newEnemy);
            newEnemy.setTarget(currentPlayer);
        }
    }

    public void adjustDifficulty() {
        max_enemies = Math.min(MAX_ENEMIES_HARD, 3 + ticks/500);
        heavy_proportion = Math.max(70, 95 - ticks/300);
        mid_proportion = Math.max(35, 70 -ticks/300);
        min_spawn = Math.max(MIN_SPAWN_TIME,15 - ticks/500);
        max_spawn = Math.max(MAX_SPAWN_TIME,35-ticks/500);
    }
}
