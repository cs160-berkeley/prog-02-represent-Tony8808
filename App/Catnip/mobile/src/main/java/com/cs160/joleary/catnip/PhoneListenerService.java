package com.cs160.joleary.catnip;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class PhoneListenerService extends WearableListenerService {

//   WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
private static final String TOAST = "/send_toast";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if( messageEvent.getPath().equalsIgnoreCase(TOAST) ) {

            // Value contains the String we sent over in WatchToPhoneService, "good job"
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            // Make a toast with the String
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, value, duration);
            toast.show();
            String info = "Details";


            Intent seeDetails = new Intent(context, Details.class);
            seeDetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            seeDetails.putExtra("Info", info);

            startActivity(seeDetails);


            // so you may notice this crashes the phone because it's
            //''sending message to a Handler on a dead thread''... that's okay. but don't do this.
            // replace sending a toast with, like, starting a new activity or something.
            // who said skeleton code is untouchable? #breakCSconceptions

        } else if (messageEvent.getPath().equalsIgnoreCase("/shake")) {

            Context context = getApplicationContext();

            Intent seeList = new Intent(context, Listing.class);
            seeList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Random randomGenerator = new Random();
            int longitude = randomGenerator.nextInt(180) - 90;
            int lattitude = randomGenerator.nextInt(360) - 180;
            String location =  "Lat: "+ String.valueOf(lattitude) + "Long: " + String.valueOf(longitude);
            seeList.putExtra("Zip Code", location);
            seeList.putExtra("shake", "True");

            startActivity(seeList);

        } else {
            super.onMessageReceived( messageEvent );
        }

    }
}
