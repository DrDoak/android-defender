package com.unorthwestern.luoleizhao2018.defender;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
//
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

import android.location.Location;
import android.location.LocationManager;
import android.content.Context;
//
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.PublicKey;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.widget.Toast;

public class SearchActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker userMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setUpMapIfNeeded();
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                Context context = getApplicationContext();
                CharSequence text = "Set New Latitude to: " + point.latitude + "\n Set New Longitude to: " + point.longitude;
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                setLat(point.latitude);
                setLng(point.longitude);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    public void setLat(double lat) {
        ((MyApplication) this.getApplication()).setLat(lat);
    }

    public void setLng(double lng) {
        ((MyApplication) this.getApplication()).setLng(lng);
    }

    private void drawPoint(double lat, double lng) {
        LatLng lastLatLng = new LatLng(lat, lng);
        if (userMarker != null) userMarker.remove();
        userMarker = mMap.addMarker(new MarkerOptions()
                .position(lastLatLng)
                .title("You are here")
                .snippet("Your last recorded location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng), 3000, null);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        drawPoint(((MyApplication) this.getApplication()).getLat(), ((MyApplication) this.getApplication()).getLng());
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

}
