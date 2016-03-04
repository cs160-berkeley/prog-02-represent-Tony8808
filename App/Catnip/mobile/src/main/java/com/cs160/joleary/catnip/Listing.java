package com.cs160.joleary.catnip;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Listing extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String zip = intent.getStringExtra("Zip Code");

        String shake = intent.getStringExtra("shake");
        if (shake != null && shake == "True"){
            Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
            sendIntent.putExtra("CAT_NAME", zip);
            startService(sendIntent);
        }

        TextView test = (TextView) findViewById(R.id.test);
        test.setText(zip);

        Button detail_1 = (Button) findViewById(R.id.buttonDetails1);


        detail_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = "Details";

                Intent seeDetails = new Intent(v.getContext(), Details.class);
                seeDetails.putExtra("Info", info);

                startActivity(seeDetails);
            }
        });
    }

}
