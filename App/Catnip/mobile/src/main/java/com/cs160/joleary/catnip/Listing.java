package com.cs160.joleary.catnip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class Listing extends Activity{
    //private static final String TWITTER_KEY = "1MJWizvt0vYLqi0H0IKf0TaaG";
    //private static final String TWITTER_SECRET = "WLpY7WMaJO4b4x9KgK7C2eGK2dMOpt857E9IuPYyH71GcuboFY";

    private static final String TWITTER_KEY = "IelGPDPstkMKEpgkTu3PA5ULr";
    private static final String TWITTER_SECRET = "0dhEOMjXioXdFOcxHHuyeFlS0eJBUljGrGCanZZxdooGe0DMSB";

    //example: congress.api.sunlightfoundation.com/legislators/locate?latitude=60&longitude=-110&apikey=7bc328a74c0a47aa8e29ad4127d3f333
    private String apiKey = "&apikey=7bc328a74c0a47aa8e29ad4127d3f333";
    private String sun = "https://congress.api.sunlightfoundation.com";
    private String locate = "/legislators/locate?";
    private String webLat = "latitude=";
    private String webLong = "&longitude=";
    private String webZip = "zip=";

    private String sunImage = "https://theunitedstates.io/images/congress/225x275/";
    private String ext = ".jpg";

    private String zip = null;
    private String lat = null;
    private String lon = null;
    private Boolean useCurrent = false;

    private String raw = "";

    JSONObject jObject;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        //getting saved values
        SharedPreferences settings = getPreferences(0);
        zip = settings.getString("Zip Code",null);
        lat = settings.getString("Lat",null);
        lon = settings.getString("Long",null);
        useCurrent = settings.getBoolean("Current", false);

        Log.d("From pref",zip + " " + lat + " " + lon);

        Intent intent = getIntent();
        String checkNull = intent.getStringExtra("Zip Code");
        if (checkNull != null) {
            zip = checkNull;
            useCurrent = intent.getBooleanExtra("Current", false);
            lat = intent.getStringExtra("Lat");
            lon = intent.getStringExtra("Long");
        }

        Log.d("After checknull",zip + " " + lat + " " + lon);

        ///////////////////////////////////////////////////////////Get stuff from sunlight
        if(!useCurrent){ //using zip
            AsyncTask stuff = new DownloadTask().execute(sun + locate + webZip + zip + apiKey);//"http://www.google.com/"
            try {
                stuff.get();
                Log.d("Status", "get() finished");

            } catch (Exception e) {
                Log.d("Error", "ASyncTask Exception or main thread completed first");
                toast("Error getting data");
            }

            Log.d("Status", "Using zip: " + sun + locate + webZip + zip + apiKey);

        }else{ //using current location
            AsyncTask stuff = new DownloadTask().execute(sun + locate + webLat + lat + webLong + lon + apiKey);
            try {
                stuff.get();
                Log.d("Status", "get() finished");

            } catch (Exception e) {
                Log.d("Error", "ASyncTask Exception or main thread completed first");
                toast("Error getting data");
            }

            Log.d("Status", "Using lat and long: " + sun + locate + webLat + lat + webLong + lon + apiKey);

        }

        ////////////////////////////////////////////////////send to watch
        //shake update, probably doesn't work
        //DO THIS AT END.
        /*Boolean shake = intent.getBooleanExtra("Shake", false);
        if (shake){
            Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
            sendIntent.putExtra("CAT_NAME", zip);
            startService(sendIntent);
        }
        */


    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = getPreferences(0);
        if(settings.getBoolean("Paused",false)) {
            Log.d("onResume:", "zip = " + settings.getString("Zip Code", "failed"));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences settings = getPreferences(0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("Paused", true);
        editor.putBoolean("Current", useCurrent);
        editor.putString("Zip Code", zip);
        editor.putString("Lat",lat);
        editor.putString("Long",lon);

        editor.commit();
    }

    //GETTING FROM INTERNET
    //USE EXAMPLE:       new DownloadTask().execute("http://www.google.com/");
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
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        ////////////////////////////////DO EVERYTHING HERE
        @Override
        protected void onPostExecute(String result) {
            try {
                jObject = new JSONObject(result);
                Log.d("Result: ", result);
                raw = result;
                JSONArray jArr = jObject.getJSONArray("results");

                /////////////////////////PARSE JSON
                JSONObject obj1 = jArr.getJSONObject(0);
                JSONObject obj2 = jArr.getJSONObject(1);
                JSONObject obj3 = jArr.getJSONObject(2);
                String count = String.valueOf(jObject.getInt("count"));
                Log.d("Result: ", "Getting count:" + count);
                if (jObject.getInt("count") > 3){
                    toast("Showing only 3 of "+ count + " possible reps. Consider using Phone Location for better accuracy.");
                }

                int Rep = ContextCompat.getColor(getApplicationContext(), R.color.Republican);
                int Dem = ContextCompat.getColor(getApplicationContext(), R.color.Democrat);
                int Ind = ContextCompat.getColor(getApplicationContext(), R.color.Indep);

                final String bio1 = obj1.getString("bioguide_id");
                String fName1 = obj1.getString("first_name");
                String lName1 = obj1.getString("last_name");
                String party1 = obj1.getString("party");
                final String website1 = obj1.getString("website");
                final String email1 = obj1.getString("oc_email");
                final String twitter1 = obj1.getString("twitter_id");

                /////////////////////////UPDATE FIELDS ON SCREEN
                TextView displayName1 = (TextView) findViewById(R.id.name1);
                TextView displayParty1 = (TextView) findViewById(R.id.party1);
                TextView displayWeb1 = (TextView) findViewById(R.id.website1);
                TextView displayEmail1 = (TextView) findViewById(R.id.email1);

                displayName1.setText(fName1 + " " + lName1);//fName + " " + lName
                Log.d("Party:", party1);
                if (party1.equals("R")) {
                    displayParty1.setText("Republican");
                    displayParty1.setTextColor(Rep);
                } else if (party1.equals("D")){
                    displayParty1.setText("Democrat");
                    displayParty1.setTextColor(Dem);
                } else {
                    displayParty1.setText("Independent");
                    displayParty1.setTextColor(Ind);
                }
                displayWeb1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //toast("Clicked Web");
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(website1));
                        startActivity(i);
                    }
                });
                displayEmail1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //toast("Clicked Email");
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto",email1, null));
                        Log.d("E-mail", email1);
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    }
                });
                Button detail_1 = (Button) findViewById(R.id.buttonDetails1);
                detail_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent seeDetails = new Intent(v.getContext(), Details.class);
                        seeDetails.putExtra("Info", bio1);

                        startActivity(seeDetails);
                    }
                });
                new LoadImage().execute(sunImage+bio1+ext,"1");

                /////////////////////////////////TWITTER STUFF
                if (!twitter1.equals("null")) {
                    TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
                        @Override
                        public void success(Result<AppSession> appSessionResult) {
                            AppSession session = appSessionResult.data;
                            TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
                            twitterApiClient.getStatusesService().userTimeline(null, twitter1, 1, null, null, false, false, false, false, new Callback<List<Tweet>>() {
                                @Override
                                public void success(Result<List<Tweet>> listResult) {
                                    for (Tweet tweet : listResult.data) {
                                        Log.d("fabricstuff", "result: " + tweet.text + "  " + tweet.createdAt);
                                        TextView abc = (TextView) findViewById(R.id.tweet1);
                                        abc.setText(tweet.text);
                                    }
                                }

                                @Override
                                public void failure(TwitterException e) {
                                    e.printStackTrace();
                                }
                            });
                        }

                        @Override
                        public void failure(TwitterException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    TextView abc = (TextView) findViewById(R.id.tweet1);
                    abc.setText("No twitter");
                }
                ////////////////////////////////////////////////////////////////////////
                final String bio2 = obj2.getString("bioguide_id");
                String fName2 = obj2.getString("first_name");
                String lName2 = obj2.getString("last_name");
                String party2 = obj2.getString("party");
                final String website2 = obj2.getString("website");
                final String email2 = obj2.getString("oc_email");
                final String twitter2 = obj2.getString("twitter_id");

                /////////////////////////UPDATE FIELDS ON SCREEN
                TextView displayName2 = (TextView) findViewById(R.id.name2);
                TextView displayParty2 = (TextView) findViewById(R.id.party2);
                TextView displayWeb2 = (TextView) findViewById(R.id.website2);
                TextView displayEmail2 = (TextView) findViewById(R.id.email2);

                displayName2.setText(fName2 + " " + lName2);//fName + " " + lName
                displayParty2.setText(party2);
                Log.d("Party:", party2);
                if (party2.equals("R")) {
                    displayParty2.setText("Republican");
                    displayParty2.setTextColor(Rep);
                } else if (party2.equals("D")){
                    displayParty2.setText("Democrat");
                    displayParty2.setTextColor(Dem);
                } else {
                    displayParty2.setText("Independent");
                    displayParty2.setTextColor(Ind);
                }
                displayWeb2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //toast("Clicked Web");
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(website2));
                        startActivity(i);
                    }
                });
                displayEmail2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //toast("Clicked Email");
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto",email2, null));
                        Log.d("E-mail", email2);
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    }
                });
                Button detail_2 = (Button) findViewById(R.id.buttonDetails2);
                detail_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent seeDetails = new Intent(v.getContext(), Details.class);
                        seeDetails.putExtra("Info", bio2);

                        startActivity(seeDetails);
                    }
                });
                new LoadImage().execute(sunImage+bio2+ext,"2");

                /////////////////////////////////TWITTER STUFF
                if (!twitter2.equals("null")) {
                    TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
                        @Override
                        public void success(Result<AppSession> appSessionResult) {
                            AppSession session = appSessionResult.data;
                            TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
                            twitterApiClient.getStatusesService().userTimeline(null, twitter2, 1, null, null, false, false, false, false, new Callback<List<Tweet>>() {
                                @Override
                                public void success(Result<List<Tweet>> listResult) {
                                    for (Tweet tweet : listResult.data) {
                                        Log.d("fabricstuff", "result: " + tweet.text + "  " + tweet.createdAt);
                                        TextView abc = (TextView) findViewById(R.id.tweet2);
                                        abc.setText(tweet.text);
                                    }
                                }

                                @Override
                                public void failure(TwitterException e) {
                                    e.printStackTrace();
                                }
                            });
                        }

                        @Override
                        public void failure(TwitterException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    TextView abc = (TextView) findViewById(R.id.tweet2);
                    abc.setText("No twitter");
                }
                ////////////////////////////////////////////////////////////////////////////
                final String bio3 = obj3.getString("bioguide_id");
                String fName3 = obj3.getString("first_name");
                String lName3 = obj3.getString("last_name");
                String party3 = obj3.getString("party");
                final String website3 = obj3.getString("website");
                final String email3 = obj3.getString("oc_email");
                final String twitter3 = obj3.getString("twitter_id");

                /////////////////////////UPDATE FIELDS ON SCREEN
                TextView displayName3 = (TextView) findViewById(R.id.name3);
                TextView displayParty3 = (TextView) findViewById(R.id.party3);
                TextView displayWeb3 = (TextView) findViewById(R.id.website3);
                TextView displayEmail3 = (TextView) findViewById(R.id.email3);

                displayName3.setText(fName3 + " " + lName3);//fName + " " + lName
                displayParty3.setText(party3);
                Log.d("Party:", party3);
                if (party3.equals("R")) {
                    displayParty3.setText("Republican");
                    displayParty3.setTextColor(Rep);
                } else if (party3.equals("D")){
                    displayParty3.setText("Democrat");
                    displayParty3.setTextColor(Dem);
                } else {
                    displayParty3.setText("Independent");
                    displayParty3.setTextColor(Ind);
                }
                displayWeb3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //toast("Clicked Web");
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(website3));
                        startActivity(i);
                    }
                });
                displayEmail3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //toast("Clicked Email");
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto",email3, null));
                        Log.d("E-mail", email3);
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    }
                });
                Button detail_3 = (Button) findViewById(R.id.buttonDetails3);
                detail_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent seeDetails = new Intent(v.getContext(), Details.class);
                        seeDetails.putExtra("Info", bio3);

                        startActivity(seeDetails);
                    }
                });
                new LoadImage().execute(sunImage+bio3+ext,"3");

                /////////////////////////////////TWITTER STUFF
                if (!twitter3.equals("null")) {
                    TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
                        @Override
                        public void success(Result<AppSession> appSessionResult) {
                            AppSession session = appSessionResult.data;
                            TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
                            twitterApiClient.getStatusesService().userTimeline(null, twitter3, 1, null, null, false, false, false, false, new Callback<List<Tweet>>() {
                                @Override
                                public void success(Result<List<Tweet>> listResult) {
                                    for (Tweet tweet : listResult.data) {
                                        Log.d("fabricstuff", "result: " + tweet.text + "  " + tweet.createdAt);
                                        TextView abc = (TextView) findViewById(R.id.tweet3);
                                        abc.setText(tweet.text);
                                    }
                                }

                                @Override
                                public void failure(TwitterException e) {
                                    e.printStackTrace();
                                }
                            });
                        }

                        @Override
                        public void failure(TwitterException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    TextView abc = (TextView) findViewById(R.id.tweet3);
                    abc.setText("No twitter");
                }

                ////////////////////////////////////////////////////////////Parse info and send to watch
                if(!useCurrent) { //using zip
                    //get lat
                    GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyBeB_7gRweYquBnKJOmWgk3PY-oUEVEWaA");
                    try {
                        GeocodingResult[] results = GeocodingApi.geocode(context, zip).await();
                        double zipLat = results[0].geometry.location.lat;
                        double zipLon = results[0].geometry.location.lng;
                        lat = String.valueOf(zipLat);
                        lon = String.valueOf(zipLon);

                        Log.d("Zip to lat lon", lat + " " + lon);
                    } catch (Exception e){
                        Log.d("Error","Fail to get geocode");
                    }
                }
                String latlon = lat+","+lon;
                raw = bio1+";"+fName1+" "+lName1+";"+party1+"&"+ bio2+";"+fName2+" "+lName2+";"+party2+"&"+bio3+";"+fName3+" "+lName3+";"+party3 ; //part of message to be sent to watch
                new getCounty().execute("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latlon + "&key=AIzaSyBeB_7gRweYquBnKJOmWgk3PY-oUEVEWaA");

            } catch (Exception e) {
                Log.d("Error post execution: ",  "Result: " + result);
                toast("Invalid location.");
            }


        }
    }


    //////////////////////////////////Getting Rep photos
    private class LoadImage extends AsyncTask<String, String, Bitmap> {

        String who;
        Bitmap bitmap;

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
                who = args[1];
                Log.d("Image address",args[0]);
                Log.d("who is:", who);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null && who.equals("1")){
                ImageView img = (ImageView) findViewById(R.id.rep1);
                img.setImageBitmap(image);
            }else if(image != null && who.equals("2")) {
                ImageView img = (ImageView) findViewById(R.id.rep2);
                img.setImageBitmap(image);
            }else if(image != null && who.equals("3")) {
                ImageView img = (ImageView) findViewById(R.id.rep3);
                img.setImageBitmap(image);
            } else {

                Toast.makeText(Listing.this, "Image Does Not exist for rep# " + who + " or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private class getCounty extends AsyncTask<String, Void, String> {
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
                String county = "";
                String state = "";
                //Log.d("Result",result);
                JSONObject ob = new JSONObject(result);
               // Log.d("Result","a");
                JSONArray response = ob.getJSONArray("results");
                //Log.d("Result","b");
                JSONObject comps = response.getJSONObject(0);
                JSONObject comps2 = response.getJSONObject(1);
               // Log.d("Result","c");
                JSONArray addressComp = comps.getJSONArray("address_components");
                JSONArray addressComp2 = comps2.getJSONArray("address_components");
                //Log.d("Result","d");

                for(int i = 0; i < addressComp.length(); i++) {
                    JSONObject elem = addressComp.getJSONObject(i);
                    JSONArray types = elem.getJSONArray("types");
                    //Log.d("Result","Got types");
                    String admin = types.getString(0);
                    //Log.d("Result","Got admin");
                    if (admin != null && admin.equals("administrative_area_level_2")) {
                        Log.d("Got county", elem.getString("short_name"));
                        county = elem.getString("short_name");
                    }
                    if (admin != null && admin.equals("administrative_area_level_1")) {
                        Log.d("Got state", elem.getString("short_name"));
                        state = elem.getString("short_name");
                    }
                }

                for(int i = 0; i < addressComp2.length(); i++){
                    JSONObject elem2 = addressComp2.getJSONObject(i);
                    JSONArray types2 = elem2.getJSONArray("types");
                    //Log.d("Result","Got types");
                    String admin2 = types2.getString(0);
                    //Log.d("Result","Got admin");

                    if (admin2 != null && admin2.equals("administrative_area_level_2") && county.equals("")){
                        Log.d("Got county", elem2.getString("short_name"));
                        county = elem2.getString("short_name");
                    }
                    if (admin2 != null && admin2.equals("administrative_area_level_1") && state.equals("")){
                        Log.d("Got state", elem2.getString("short_name"));
                        state = elem2.getString("short_name");
                    }
                }
               // Log.d("Result","e");
                Log.d("Success", county + "," + state);

                InputStream assStream = getAssets().open("election-county-2012.json");
                Log.d("Status", "Opened Stream");
                int size = assStream.available();
                byte[] buffer = new byte[size];
                assStream.read(buffer);
                assStream.close();
                String jsonString = new String(buffer, "UTF-8");

                JSONArray data = new JSONArray(jsonString);
                //Log.d("election string", jsonString);

                String obama = "null";
                String rom = "null";

                for(int i = 0; i < data.length(); i++){
                    JSONObject elem = data.getJSONObject(i);
                    String st = elem.getString("state-postal");
                    String ct = elem.getString("county-name");
                    //Log.d("Scanning", ct+","+st);
                    if (st.equals(state) && county.contains(ct)){
                        obama = elem.getString("obama-percentage");
                        rom = elem.getString("romney-percentage");
                        Log.d("Got match", obama+","+rom);
                        break;
                    }

                }

                //append data to raw and send to watch
                raw = raw + "&" + county + "," + state + ";" + obama + ";" + rom;

                Log.d("Sending to watch:", raw);
                Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                sendIntent.putExtra("Message", raw);
                startService(sendIntent);
            } catch (Exception e) {
                Log.d("Error, result is", result);
            }
        }
    }

    private void toast(String val){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, val, duration);
        toast.show();
    }


}
