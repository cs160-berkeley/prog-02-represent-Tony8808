package com.cs160.joleary.catnip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;

public class Candidates extends Activity implements SensorEventListener {

    private TextView mTextView;
    protected String zip = "";
    protected String[] bio = new String[3];
    protected String[] name = new String[3];
    protected String[] party = new String[3];
    protected Bitmap[] pics = new Bitmap[3];
    protected String county;
    protected String oba;
    protected String rom;

    private String sunImage = "https://theunitedstates.io/images/congress/225x275/";
    private String ext = ".jpg";

    //Used code from http://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125
    //for accelerometer

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidates);
        final Resources res = getResources();

        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);

        pager.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                // Adjust page margins:
                //   A little extra horizontal spacing between pages looks a bit
                //   less crowded on a round display.
                final boolean round = insets.isRound();
                int rowMargin = res.getDimensionPixelOffset(R.dimen.page_row_margin);
                int colMargin = res.getDimensionPixelOffset(round ?
                        R.dimen.page_column_margin_round : R.dimen.page_column_margin);
                pager.setPageMargins(rowMargin, colMargin);

                // GridViewPager relies on insets to properly handle
                // layout for round displays. They must be explicitly
                // applied since this listener has taken them over.
                pager.onApplyWindowInsets(insets);
                return insets;
            }


        });

        //get zip value
        Intent intent = getIntent();
        Bundle extra = intent.getExtras();

        zip = extra.getString("Zip");

        String[] parts = zip.split("&");
        for (int i = 0; i<parts.length - 1;i++){
            String[] elems = parts[i].split(";");
            bio[i] = elems[0];
            name[i] = elems[1];
            party[i] = elems[2];
            Log.v("elements",bio[i] + " " + name[i] + " " + party[i]  );
        }

        //getting images
        try {
            new LoadImage().execute(sunImage+bio[0]+ext,"0").get();
            new LoadImage().execute(sunImage+bio[1]+ext,"1").get();
            new LoadImage().execute(sunImage+bio[2]+ext,"2").get();
        }catch (Exception e){
            Log.d("get failed","yes");
        }

        String[] perc = parts[parts.length - 1].split(";");
        county = perc[0];
        oba = perc[1];
        rom = perc[2];
        Log.v("percentages", oba + " " + rom);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        pager.setAdapter(new SampleGridPagerAdapter(this, getFragmentManager()));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, "SHAKING", duration);
                    toast.show();

                    //tell phone to update location
                    Intent sendIntent = new Intent(this, SendShake.class);
                    sendIntent.putExtra("Shake","True");
                    startService(sendIntent);
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        return;
    }

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {

        Bitmap bitmap;
        String who;

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
                who = args[1];
                Log.d("Image address", args[0]);
                Log.d("Who for image",who);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            int i = Integer.valueOf(who);

            if(image != null){
                pics[i] = image;

            } else {

                Toast.makeText(Candidates.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
