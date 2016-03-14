package com.cs160.joleary.catnip;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class CustomFragment extends Fragment {

    TextView na;
    TextView pa;
    ImageButton button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        na = (TextView) getView().findViewById(R.id.name);
        pa = (TextView) getView().findViewById(R.id.party);
        button = (ImageButton) getView().findViewById(R.id.imageButton);

        Bundle bun = getArguments();
        int who = bun.getInt("Who");

        //get from candidates activity fields
        Candidates can = (Candidates) getActivity();

        String name = can.name[who];
        String party = can.party[who];
        final String bio = can.bio[who];

        na.setText(name);

        if (party.equals("R")) {
            pa.setText("Republican");
            pa.setTextColor(getResources().getColor(R.color.Republican));
        } else if (party.equals("D")){
            pa.setText("Democrat");
            pa.setTextColor(getResources().getColor(R.color.Democrat));
        } else {
            pa.setText("Independent");
            pa.setTextColor(getResources().getColor(R.color.Indep));
        }

        Log.v("Who",String.valueOf(who)+ " " + bio + " " + name + " " + party);

        //text.setText("HELLO");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send to phone from watch
                Intent sendIntent = new Intent(getContext(), WatchToPhoneService.class);
                sendIntent.putExtra("Bio",bio);
                getContext().startService(sendIntent);

            }
        });

        button.setImageBitmap(can.pics[who]);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("CustomFragment", "onActivityCreated()");





        return inflater.inflate(R.layout.activity_custom_fragment, container, false);

    }


}
