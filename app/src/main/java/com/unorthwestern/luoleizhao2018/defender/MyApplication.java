package com.unorthwestern.luoleizhao2018.defender;
import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    private double lat;
    private double lng;

    public double getLat() {
        return lat;
    }
    public void setLat(double someVariable) {
        this.lat = someVariable;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double someVariable) {
        this.lng = someVariable;
    }
}