package com.cs160.joleary.catnip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "IelGPDPstkMKEpgkTu3PA5ULr";
    private static final String TWITTER_SECRET = "0dhEOMjXioXdFOcxHHuyeFlS0eJBUljGrGCanZZxdooGe0DMSB";


    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    //private static final String TWITTER_KEY = "1MJWizvt0vYLqi0H0IKf0TaaG";
    //private static final String TWITTER_SECRET = "WLpY7WMaJO4b4x9KgK7C2eGK2dMOpt857E9IuPYyH71GcuboFY";

    private GoogleApiClient mGoogleApiClient;
    String mLatitudeText = "";
    String mLongitudeText = "";


    //there's not much interesting happening. when the buttons are pressed, they start
    //the PhoneToWatchService with the cat name passed in.

    private Button zipButton;
    private Button locationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        //Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Wearable.API)  // used for data layer API
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



        zipButton = (Button) findViewById(R.id.enterZip);
        locationButton = (Button) findViewById(R.id.thisLocation);

        zipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView zipCode = (TextView) findViewById(R.id.zipCode);
                Intent seeList = new Intent(v.getContext(), Listing.class);
                String zip = zipCode.getText().toString();

                if (!zip.isEmpty()) {

                    seeList.putExtra("Zip Code", zip);
                    seeList.putExtra("Shake", false);
                    seeList.putExtra("Current", false);
                    seeList.putExtra("Lat", "None");
                    seeList.putExtra("Long", "None");

                    try {

                    } catch (Exception e){
                        Log.d("Error","failed to read");
                    }

                    startActivity(seeList);
                } else{
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, "No zip code entered", duration);
                    toast.show();
                }


            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent seeList = new Intent(v.getContext(), Listing.class);

                seeList.putExtra("Zip Code", "None");
                seeList.putExtra("Shake", false);
                seeList.putExtra("Current", true);
                seeList.putExtra("Lat",mLatitudeText);
                seeList.putExtra("Long",mLongitudeText);

                startActivity(seeList);

            }
        });

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

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient); //should be fine
            if (mLastLocation != null) {
                mLatitudeText = String.valueOf(mLastLocation.getLatitude());
                mLongitudeText = String.valueOf(mLastLocation.getLongitude());
                Log.d("Got current location", mLatitudeText + " " + mLongitudeText);
            }
        } catch (SecurityException e){
            Log.d("Error","Failed to get current location");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connResult) {}
}
