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

        Intent in = getActivity().getIntent();
        Bundle extras = in.getExtras();
        if (extras != null){
            Log.v("Test",in.getStringExtra("Zip"));
            //text.setText(extras.getString("Zip"));
        }

        //get from candidates field
        TextView text = (TextView) getView().findViewById(R.id.Zip);
        TextView obamaText = (TextView) getView().findViewById(R.id.obamaP);
        TextView romText = (TextView) getView().findViewById(R.id.romneyP);
        Candidates can = (Candidates) getActivity();
        String oba = can.oba;
        String rom = can.rom;
        text.setText(can.county);
        obamaText.setText("Obama " + oba + "%");
        romText.setText("Romney " + rom + "%");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_county, container, false);
    }
}
