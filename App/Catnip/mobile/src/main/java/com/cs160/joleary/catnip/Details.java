package com.cs160.joleary.catnip;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Details extends Activity {



    private String apiKey = "&apikey=7bc328a74c0a47aa8e29ad4127d3f333";
    private String sun = "https://congress.api.sunlightfoundation.com";
    private String sunImage = "https://theunitedstates.io/images/congress/original/";
    private String ext = ".jpg";

    List<String> words = new ArrayList<String>();

    int count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        count = 0;

        ///////////////////////////////////Get details of this guy from sunlight

        Intent intent = this.getIntent();
        String bio = intent.getStringExtra("Info");

        new LoadImage().execute(sunImage+bio+ext);
        Log.d("Status", "finished get image");

        new DownloadTask().execute("https://congress.api.sunlightfoundation.com/legislators?bioguide_id=" +  bio + apiKey);
        Log.d("Status", "finished get rep info");

        new DownloadTask().execute("https://congress.api.sunlightfoundation.com/committees?member_ids=" + bio + apiKey);
        Log.d("Status", "finished get commitees");

        new DownloadTask().execute("https://congress.api.sunlightfoundation.com/bills?sponsor_id=" + bio + apiKey);
        Log.d("Status", "finished get bills");

    }


    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            //do your request in here so that you don't interrupt the UI thread
            try {
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                words.add(result);
                //Log.d("Response",result);
                count++;
                if (count == 3){
                    int Rep = ContextCompat.getColor(getApplicationContext(), R.color.Republican);
                    int Dem = ContextCompat.getColor(getApplicationContext(), R.color.Democrat);
                    int Ind = ContextCompat.getColor(getApplicationContext(), R.color.Indep);

                    for(String x:words){
                        Log.d("Array contents",x);
                    }

                    JSONObject rep = new JSONObject(words.get(0));
                    JSONArray repArray = rep.getJSONArray("results");
                    Log.d("Status","got rep");

                    JSONObject com = new JSONObject(words.get(1));
                    JSONArray comArray = com.getJSONArray("results");
                    Log.d("Status","got coms");

                    JSONObject bills = new JSONObject(words.get(2));
                    JSONArray billsArray = bills.getJSONArray("results");
                    Log.d("Status","got bills");

                    //details
                    JSONObject obj1 = repArray.getJSONObject(0);
                    String fName1 = obj1.getString("first_name");
                    String lName1 = obj1.getString("last_name");
                    String party1 = obj1.getString("party");
                    String term1 = obj1.getString("term_end");

                    TextView name = (TextView) findViewById(R.id.name);
                    TextView party = (TextView) findViewById(R.id.party);
                    TextView term = (TextView) findViewById(R.id.term);

                    name.setText(fName1 + " " + lName1);//fName + " " + lName
                    Log.d("Party:", party1);
                    if (party1.equals("R")) {
                        party.setText("Republican");
                        party.setTextColor(Rep);
                    } else if (party1.equals("D")){
                        party.setText("Democrat");
                        party.setTextColor(Dem);
                    } else {
                        party.setText("Independent");
                        party.setTextColor(Ind);
                    }
                    term.setText(term1);

                    //committees
                    int comCount = 0;
                    String text = "";
                    for(int i = 0; i < comArray.length(); i++){
                        JSONObject elem = comArray.getJSONObject(i);
                        String comName = elem.getString("name");
                        Log.d("Committees",comName);
                        comCount++;
                        if (comCount>4){
                            break;
                        }
                        text = text + "-" + comName + "\n";
                    }
                    TextView comName = (TextView) findViewById(R.id.commit);
                    comName.setText(text);

                    //bills
                    int billCount = 0;
                    String text2 = "";
                    for(int i = 0; i < billsArray.length(); i++){
                        JSONObject elem = billsArray.getJSONObject(i);
                        String billName = elem.getString("short_title");
                        String intro = elem.getString("introduced_on");

                        if (billCount > 3){
                            break;
                        }

                        if(!billName.equals("null")) {
                            Log.d("Bills", billName + " " + intro);
                            billCount++;
                            text2 = text2 + intro + ": " + billName + "\n";
                        }
                    }
                    TextView billName = (TextView) findViewById(R.id.bills);
                    billName.setText(text2);
                }
            } catch (Exception e) {
                Log.d("Error, result is", result);
            }
        }
    }


    //////////////////////////////////Getting Rep photos
    private class LoadImage extends AsyncTask<String, String, Bitmap> {

        Bitmap bitmap;

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
                Log.d("Image address", args[0]);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){
                ImageView img = (ImageView) findViewById(R.id.picture);
                img.setImageBitmap(image);

            } else {

                Toast.makeText(Details.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }

}
