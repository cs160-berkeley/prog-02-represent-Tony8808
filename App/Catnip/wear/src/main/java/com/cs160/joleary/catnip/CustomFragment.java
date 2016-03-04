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

    //public CustomFragment (int page){

    //}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v("CustomFragment", "onActivityCreated()");
        TextView text = (TextView) getView().findViewById(R.id.testt);
        ImageButton button = (ImageButton) getView().findViewById(R.id.imageButton);


        //text.setText("HELLO");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send to phone from watch
                Intent sendIntent = new Intent(getContext(), WatchToPhoneService.class);
                getContext().startService(sendIntent);

                //testing
                //Intent test = new Intent(getContext(), MainActivity.class);
                //startActivity(test);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_custom_fragment, container, false);
    }
}
