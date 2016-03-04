package com.cs160.joleary.catnip;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class County extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v("County", "onActivityCreated()");
        TextView text = (TextView) getView().findViewById(R.id.Zip);
        Intent in = getActivity().getIntent();
        Bundle extras = in.getExtras();
        if (extras != null){
            //text.setText(extras.getString("Zip"));
        }
        Candidates can = (Candidates) getActivity();
        text.setText(can.zip);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_county, container, false);
    }
}
