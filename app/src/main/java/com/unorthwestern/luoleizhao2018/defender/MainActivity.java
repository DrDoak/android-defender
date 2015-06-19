package com.unorthwestern.luoleizhao2018.defender;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.ConnectionResult;
import static android.widget.EditText.*;


public class MainActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    GoogleApiClient mGoogleApiClient;
    private LocationManager locMan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateEditText();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateEditText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void searchCoordinates(View view){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }
    public void moreInfo(View view) {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    public void useCurrent(View view) {
        //obtain current Location
        setCurrentLocation();
    }

    public void startGame(View view) {
        //obtain current Location
        EditText EditLat = (EditText) findViewById(R.id.editText);
        EditText EditLng = (EditText) findViewById(R.id.editText2);
        System.out.println(EditLat.getText().toString());
        if (EditLat.getText().toString() == "")
            EditLat.setText("0", TextView.BufferType.EDITABLE);
        if (EditLng.getText().toString() == "")
            EditLng.setText("0", TextView.BufferType.EDITABLE);
        double lat = Double.parseDouble(EditLat.getText().toString());
        double lng = Double.parseDouble(EditLng.getText().toString());
        ((MyApplication) this.getApplication()).setLat(lat);
        ((MyApplication) this.getApplication()).setLng(lng);
        Intent intent = new Intent(this, PreviewActivity.class);
        startActivity(intent);
    }


    private void updateEditText() {
        EditText EditLat = (EditText) findViewById(R.id.editText);
        EditText EditLng = (EditText) findViewById(R.id.editText2);
        String lat = Double.toString(((MyApplication)this.getApplication()).getLat());
        String lng = Double.toString(((MyApplication)this.getApplication()).getLng());
        EditLat.setText(lat, TextView.BufferType.EDITABLE);
        EditLng.setText(lng, TextView.BufferType.EDITABLE);
    }
    private void setCurrentLocation() {
        locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (lastLoc != null) {
            double lat = lastLoc.getLatitude();
            double lng = lastLoc.getLongitude();
            ((MyApplication) this.getApplication()).setLat(lat);
            ((MyApplication) this.getApplication()).setLng(lng);
        } else {
            Context context = getApplicationContext();
            CharSequence text = "Cannot Locate Current Position!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            double lat = 40.198842;
            ((MyApplication) this.getApplication()).setLat(lat);
            double lng = -83.010085;
            ((MyApplication) this.getApplication()).setLng(lng);
        }
        updateEditText();
    }
}
