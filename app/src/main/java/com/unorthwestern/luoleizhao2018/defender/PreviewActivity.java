package com.unorthwestern.luoleizhao2018.defender;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.provider.SyncStateContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
//
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
//
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class PreviewActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker userMarker;
    private Marker[] placeMarkers;
    private final int MAX_PLACES = 20;
    private MarkerOptions[] places;
    public String[] venueType = new String[25];
    public double[] xCoordinates = new double[25];
    public double[] yCoordinates = new double[25];
    int currentIndex;
    private boolean done;
    private String backgroundPath;
    public Activity myActivity = this;
    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        done = false;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        placeMarkers = new Marker[MAX_PLACES];
        File fl = getFilesDir();
        backgroundPath = fl.toString() + "/map_background.png";
        setContentView(R.layout.activity_preview);
        setUpMapIfNeeded();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_preview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void drawPoint(double lat, double lng) {
        LatLng lastLatLng = new LatLng(lat, lng);
        if (userMarker != null) userMarker.remove();
        userMarker = mMap.addMarker(new MarkerOptions()
                .position(lastLatLng)
                .title("Current Coordinate"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng,12.0f), 3000, null);
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
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        String search = generateSearchString("test");
        new GetPlaces().execute(search);
    }

    private String generateSearchString(String type) {
        String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                "json?location=" + ((MyApplication) this.getApplication()).getLat() + ","
                + ((MyApplication) this.getApplication()).getLng() +
                "&radius=1000&sensor=true" +
                "&types=" + "food|bank|store|library|airport|school" +
                "&key=AIzaSyBGhFDln2lcZXb_LGPpP_Y8FzyzlQyu-JA";
        return placesSearchStr;
    }

    public void returnToMain(View view) {
        finish();
    }

    public void startGame(View view) {
        done = true;
        if(placeMarkers!=null){
            for(int pm=0; pm<placeMarkers.length; pm++){
                if(placeMarkers[pm]!=null)
                    placeMarkers[pm].remove();
            }
        }
        prepareLocations();
        Runnable task = new Runnable() {
            public void run() {
                GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
                    Bitmap bitmap;

                    @Override
                    public void onSnapshotReady(Bitmap snapshot) {
                        bitmap = snapshot;
                        try {
                            File fl = getFilesDir();
                            FileOutputStream out = new FileOutputStream(fl.toString() + "/map_background.png");
                            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                mMap.snapshot(callback);
                Intent intent = new Intent();
                intent.setClass(myActivity, MainGame.class);
                intent.putExtra("types", venueType);
                intent.putExtra("xCoordinates", xCoordinates);
                intent.putExtra("yCoordinates",yCoordinates);
                intent.putExtra("backgroundPath",backgroundPath);
                startActivity(intent);
            }
        };
        worker.schedule(task, 100, TimeUnit.MILLISECONDS);
    }
    private void prepareLocations() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        LatLng NE = bounds.northeast;
        LatLng SW = bounds.southwest;
        double min = 0;
        double yMax = Math.abs(NE.latitude - SW.latitude);
        double xMax = Math.abs(SW.longitude - NE.longitude);
        for (int i = 0;i < venueType.length;i++) {
            if (xCoordinates[i] != 0) {
                xCoordinates[i] = Math.abs(xCoordinates[i] - SW.longitude);
                yCoordinates[i] = Math.abs(yCoordinates[i] - NE.latitude);
                xCoordinates[i] = ((xCoordinates[i] * size.x) / xMax);
                yCoordinates[i] = ((yCoordinates[i] * size.y) / yMax);
            }
        }
    }

    class GetPlaces extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... placesURL) {
            StringBuilder placesBuilder = new StringBuilder();
            //process search parameter string(s)
            for (String placeSearchURL : placesURL) {
                HttpClient placesClient = new DefaultHttpClient();
                try {
                    //try to fetch the data
                    URL myURL = new URL(placeSearchURL);
                    InputStream placesContent = ((InputStream) myURL.getContent());
                    InputStreamReader placesInput = new InputStreamReader(placesContent);
                    BufferedReader placesReader = new BufferedReader(placesInput);
                    String lineIn;
                    while ((lineIn = placesReader.readLine()) != null) {
                        placesBuilder.append(lineIn);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return placesBuilder.toString();
//            return "{   "html_attributions" : [],   "results" : [      {         "geometry" : {            "location" : {               "lat" : 40.204699,               "lng" : -83.020332            }         },         "icon" : "http://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png",         "id" : "bb11baf8ff6b816de25fe94bff013433f2c50dd7",         "name" : "Olentangy High School",         "opening_hours" : {            "open_now" : false,            "weekday_text" : []         },         "place_id" : "ChIJy7hPqWzxOIgRsftj91A-YGY",         "reference" : "CnRoAAAAGExI90qzlWRFJrEZys8itiH8edvMr7tFY4Th5LYJX4sVRS_BGtV889zlbdCku6NHMEhtpRj2-xXxDCYJS2J3FkMp9IvgX1xsqnmjQiCEcz2cz3SFi1F9N22W0kIEOGm7PKR3zG0vrrR0x5R1h02QtRIQ4Ozy4LUktfdYGw2rjBRHmxoUYokm0JHW1iqSiKXr32MQ_hN-4vk",         "scope" : "GOOGLE",         "types" : [ "school", "establishment" ],         "vicinity" : "675 Lewis Center Road, Lewis Center"      },      {         "geometry" : {            "location" : {               "lat" : 40.198396,               "lng" : -83.010184            }         },         "icon" : "http://maps.gstatic.com/mapfiles/place_api/icons/jewelry-71.png",         "id" : "1c4385bb2894c9a5c82eef6be650c0c6a78325be",         "name" : "Be Frilled, Inc.",         "photos" : [            {               "height" : 992,               "html_attributions" : [],               "photo_reference" : "CnRnAAAALJfCKYGsu2hkznuwXHHH3FYgpghfzwDkKRw1_aoH9CpBYdHKM4NjuQxz1CLpWyG4XtiMQjQZyHX-059QZv7RH1SwJ18_rls32DRPqSOTeBkRsila9f-wOwT7S7jWquPCxL9lY5aJo3fMeJhev7WMtRIQnIqwb1X76UsQfi1HEV0sZhoUmqXpN0nMjGNmWyzfeFNLGRXOFvg",               "width" : 1200            }         ],         "place_id" : "ChIJAQAAAFDxOIgRFamxd7aMsX4",         "reference" : "CnRjAAAAQcPKRE-N_IVh40KBrSz9Jo6R0kXF20dBoKpEVeUPJBgyh3PDbz0T0_tGPevTAU89grTKhN-H_NblLT7PjmXjduZumX8ap0hpBxnPMCHu4xa6suHY3bjYP-a-pg5FQ6GNqtsL7D3KAfuyA6Og0nOh5xIQdLJqMT7uFNaHllbc8nPZIxoU1SmpLpqpJQoAizAYDRvwrE4CwcE",         "scope" : "GOOGLE",         "types" : [ "jewelry_store", "store", "establishment" ],         "vicinity" : "Lewis Center"      },      {         "geometry" : {            "location" : {               "lat" : 40.198396,               "lng" : -83.010184            }         },         "icon" : "http://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png",         "id" : "92fdfd06125a7ccf7966ed8bfb1ed823f809c0a7",         "name" : "BoCha Electric",         "opening_hours" : {            "open_now" : true,            "weekday_text" : []         },         "photos" : [            {               "height" : 326,               "html_attributions" : [],               "photo_reference" : "CnRnAAAAn8bWylAIesadWou9FwfUMRYHaMeCmHjdITNfI5K3XxIAYWuez8LsgmZHFMo1U_Z6n4X5wZV5kmvzmfl5kaIMWGbMNszFKfAEzAXc-doQQgd3xf5snctgofbSaFp64wIM9jGETU_vkQRvFHdXOpBxKRIQWypyO4Hev6JSwsC7ITzRuBoUEyCXE37pNN9bQEnEKjhqBOhMoUI",               "width" : 312            }         ],         "place_id" : "ChIJI688xEDxOIgRIr4EaBo19yo",         "reference" : "CnRhAAAAUUB5fVcM2uDB1LOd9vzOxYFq5W3V0eIDdeAxO6boFgFSWDQxJTshQ7GAkKi0PwaOcdSJ2iOF1CAwU-oA_KLdvEM2zdyhvCQIEHM4lLe4kboD1F1MT6EMxfeP9VaNb0EXiB66yfUNOQ8OCKtJ1S67QxIQuEkam4_f3iT8SJXfU-fN9xoU-QIbOogXAtgAvDO_y6l42dswDGM",         "scope" : "GOOGLE",         "types" : [ "store", "establishment" ],         "vicinity" : "Lewis Center"      },      {         "geometry" : {            "location" : {               "lat" : 40.198993,               "lng" : -83.008819            }         },         "icon" : "http://maps.gstatic.com/mapfiles/place_api/icons/repair-71.png",         "id" : "696d372d5a14cbf8a90d9e183bc4be7167aa5570",         "name" : "Pine Forest Products Inc",         "place_id" : "ChIJR1bNTkfxOIgRGH_iJqq3830",         "reference" : "CnRrAAAAE92lWrA10x7kJkE-KRujAznTELvxXwoO_MjpI_qdG9jU6ajxb4P8y4Lw5afiq92Yir_BUe_v4XWmLzo7UESsFZZwesx4kAPJO7xhxiC1SCX5SgzT27HDJtUQWiFtx7l5ibg6zoCfuVDSFRV4hQ-_cBIQKcrb6fOIkRWS9N287Pz2OxoUz1hd7SDqZJ0AJQp0eoEL3xjaJc8",         "scope" : "GOOGLE",         "types" : [ "hardware_store", "store", "establishment" ],         "vicinity" : "1550 Lewis Center Road # D, Lewis Center"      },      {         "geometry" : {            "location" : {               "lat" : 40.200416,               "lng" : -83.015321            }         },         "icon" : "http://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png",         "id" : "5aba4557b96b747858ee7f45628df03ab92d30d8",         "name" : "Lewis Center Preschool",         "place_id" : "ChIJ5xqgUhXxOIgRTRoWf8di-P4",         "reference" : "CnRqAAAAPW5XR9sfVb0aueeMrxT4SzmFMCiswFVf5QOzR-I1b-2VkzMHZDyiPlFt1wcPg-pQsyFqWQtG7h8Mu8_-IDmWgxfSIbrTG3mbCEO5cOYeoAY_J0wAvUVzcSusOaOfH1v2hkhiuwDUr7O0AGLJf5osMxIQySywNUwONWlRpG9_ia-jiRoUavTYEOzyyl7A2VnVGAiXC2sNtGk",         "scope" : "GOOGLE",         "types" : [ "school", "establishment" ],         "vicinity" : "1081 Lewis Center Road, Lewis Center"      },      {         "geometry" : {            "location" : {               "lat" : 40.19902,               "lng" : -83.009277            }         },         "icon" : "http://maps.gstatic.com/mapfiles/place_api/icons/shopping-71.png",         "id" : "8de210259a39e4351f994ec8f7d9e3914a6c1800",         "name" : "Focus Convenience Market",         "opening_hours" : {            "open_now" : false,            "weekday_text" : []         },         "photos" : [            {               "height" : 960,               "html_attributions" : [],               "photo_reference" : "CnRnAAAAgqhJvslktrH_lDN9d6uVWQeIAh3wIspUkP2wcgM2ZuAqa5SE3z7sXoR5Gjw1JvibjmfnvTuQTKoplX9MBV67Jk3iPb0l9vJJiEAnQIghXomc-_8TDKvIvcXEU7HXO0wIkwxPkdAv2OimSKTTdrOn6RIQ8DYhSvk2Xweh1rMIGL9gGxoUCRgq_8cXulrqSb7D5SEPaXgIPA4",               "width" : 826            }         ],         "place_id" : "ChIJi6aqTEfxOIgR1pAPNW-GbWw",         "reference" : "CnRrAAAA0VM1fgHKmh-6O5x42jGFHeABi7C93Q4S0n1VBojbzaErOtzaQipKlryrCOJv2Z_oKIYl0QNCPCECu-Sk95rNwzdQA3Uvw9m2yX5LUrT8L4ImYbWZnKLW0qwSX4IlfcF9QEdATcP8dKPrObS8vPwG5RIQ5dEMH9OWlCAzcM58LMl-shoUksMT-WyimHqUyDsTcvXwK-FYGGc",         "scope" : "GOOGLE",         "types" : [ "convenience_store", "food", "store", "establishment" ],         "vicinity" : "1520 Lewis Center Road, Lewis Center"      }   ],   "status" : "OK"}";        }
        }

        protected void onPostExecute(String result) {
            int wasteTime;
            if (done) {
                wasteTime = 10;
                return;
            } else
                wasteTime = 5;
            if(placeMarkers!=null){
                for(int pm=0; pm<placeMarkers.length; pm++){
                    if(placeMarkers[pm]!=null)
                        placeMarkers[pm].remove();
                }
            }
            currentIndex = 0;
            try {
                //parse JSON
                JSONObject resultObject = new JSONObject(result);
                JSONArray placesArray = resultObject.getJSONArray("results");
                places = new MarkerOptions[Math.min(placesArray.length(), 10)];
                for (int p=0; p<Math.min(placesArray.length(), 10); p++) {
                    boolean missingValue=false;
                    LatLng placeLL=null;
                    String placeName="";
                    String vicinity="";
                    //int currIcon = otherIcon;
                    try{
                        //attempt to retrieve place data values
                        missingValue=false;
                        JSONObject placeObject = placesArray.getJSONObject(p);
                        JSONObject loc = placeObject.getJSONObject("geometry").getJSONObject("location");
                        placeLL = new LatLng(
                                Double.valueOf(loc.getString("lat")),
                                Double.valueOf(loc.getString("lng")));
                        JSONArray types = placeObject.getJSONArray("types");
                        venueType[currentIndex] = "store"; //set Default type
                        for(int t=0; t<types.length(); t++) {
                            String thisType = types.get(t).toString();
                            if (thisType.contains("airport")) {
                                venueType[currentIndex] = "airport";
                                break;
                            } else if (thisType.contains("bank")) {
                                venueType[currentIndex] = "bank";
                                break;
                            } else if (thisType.contains("library")) {
                                venueType[currentIndex] = "library";
                                break;
                            } else if (thisType.contains("school")) {
                                venueType[currentIndex] = "school";
                                break;
                            } else if (thisType.contains("food")) {
                                venueType[currentIndex] = "food";
                                break;
                            } else if (thisType.contains("store")){
                                venueType[currentIndex] = "store";
                                break;
                            }
                        }
                        xCoordinates[currentIndex] = Double.valueOf(loc.getString("lng"));
                        yCoordinates[currentIndex] = Double.valueOf(loc.getString("lat"));
                        currentIndex ++;
                        vicinity = placeObject.getString("vicinity");
                        placeName = placeObject.getString("name");
                    }
                    catch(JSONException jse){
                        missingValue=true;
                        jse.printStackTrace();
                    }
                    if(missingValue)    places[p]=null;
                    else
                        places[p]=new MarkerOptions()
                                .position(placeLL)
                                .title(placeName)
//                .icon(BitmapDescriptorFactory.fromResource(currIcon))
                                .snippet(vicinity);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if(places!=null && placeMarkers!=null) {
                for (int p = 0; p < places.length && p < placeMarkers.length; p++) {
                    //will be null if a value was missing
                    if (places[p] != null) {
                        placeMarkers[p] = mMap.addMarker(places[p]);
                    }
                }
                //Calculate the markers to get their position
                LatLngBounds.Builder b = new LatLngBounds.Builder();
                for (int i = 0; i < venueType.length; i++) {
                    if (xCoordinates[i] != 0) {
                        LatLng ll = new LatLng(yCoordinates[i], xCoordinates[i]);
                        b.include(ll);
                    }
                }
                if (venueType != null) {
                    LatLngBounds bounds = b.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 64);
                    mMap.animateCamera(cu);
                }
            }
        }
    }

}
