package com.cs160.joleary.catnip;

import android.os.Bundle;
import android.app.Activity;

public class Details extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
