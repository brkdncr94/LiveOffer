package cs310.brkdncr.liveoffer_deneme2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brkdn on 28/04/2016.
 */
public class ListActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    ProgressDialog progressDialog;
    TextView locationText;
    ArrayList<String> categoriesArray = null; // to be used when constructing the query for database
    ArrayList<Offer> offers;
    GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    private AddressResultReceiver mResultReceiver;
    double currentLatitude; // = 40.892258; // These hard-coded values were used for debugging on an emulator
    double currentLongitude; // = 29.383082;

    String city = "Default"; // default value, if we see this, there is a problem
    String ipAdr;
    String serverURL = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ipAdr = MainActivity.ipAdr;
        categoriesArray = this.getIntent().getStringArrayListExtra("CategoriesArray");

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks((ConnectionCallbacks) this)
                    .addOnConnectionFailedListener((OnConnectionFailedListener) this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //***************************************************************************************
        // FOR DEBUG PURPOSES
        String categories = categoriesArray.get(0);
        for (int i = 1; i < categoriesArray.size(); i++) {
            categories = categories + " OR " + categoriesArray.get(i);
        }
        Toast.makeText(this, "Selected categories: " + categories, Toast.LENGTH_SHORT).show();
        //***************************************************************************************


    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    public void onConnected(Bundle connectionHint) {
        createProgressDialog();
        getCurrentLocation();
        //TalkToServerTask tsk = new TalkToServerTask(); // RUN THIS LINE TO TALK TO SIMPLE SOCKET SERVER
        TalkToCloudServerTask tsk = new TalkToCloudServerTask(); // RUN THIS LINE TO TALK TO REST SERVER
        Log.i("DEV", "Going to execute Async Task.");
        tsk.execute(serverURL);
    }

    protected void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            progressDialog.dismiss();
            Toast.makeText(ListActivity.this,"Izin yok sana!", Toast.LENGTH_SHORT).show();
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            //String lat = String.valueOf(mLastLocation.getLatitude());
            //String lon = String.valueOf(mLastLocation.getLongitude());
            currentLatitude = mLastLocation.getLatitude();
            currentLongitude = mLastLocation.getLongitude();

            //locationText = (TextView) findViewById(R.id.locationText);
            //locationText.setText(lat + " | " + lon);

            if (!Geocoder.isPresent()) {
                Toast.makeText(this, "ERROR: No Geocoder Available.", Toast.LENGTH_LONG).show();
                return;
            }
            startIntentService();
        }
    }

    protected void startIntentService() {
        mResultReceiver = new AddressResultReceiver(new Handler());
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        //intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        intent.putExtra("Latitude", currentLatitude);
        intent.putExtra("Longitude", currentLongitude);
        startService(intent);
    }

    public void returnHome(View view) {
        Intent i = new Intent(ListActivity.this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void refresh(View view) {
        createProgressDialog();
        getCurrentLocation();
        //TalkToServerTask tsk = new TalkToServerTask(); // RUN THIS LINE TO TALK TO SIMPLE SOCKET SERVER
        TalkToCloudServerTask tsk = new TalkToCloudServerTask(); // RUN THIS LINE TO TALK TO REST SERVER
        tsk.execute(serverURL);
    }

    public void createProgressDialog()
    {
        progressDialog = new ProgressDialog(ListActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //progressDialog.dismiss();
    }

    public static class  OfferListFragment extends ListFragment {

        @Override
        public void onListItemClick(ListView l, View v, int position, long id)
        {
            Toast.makeText(getActivity(),"List Item Clicked", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(getActivity(),OfferActivity.class);
            String companyName = ((Offer)l.getItemAtPosition(position)).getCompanyName();
            String offerTitle = ((Offer)l.getItemAtPosition(position)).getTitle();
            String offerDescription = ((Offer)l.getItemAtPosition(position)).getDescription();
            String companyAddress = ((Offer)l.getItemAtPosition(position)).getAddress();
            double [] coordinates = ((Offer)l.getItemAtPosition(position)).getCoordinates();
            i.putExtra("companyName",companyName);
            i.putExtra("offerTitle",offerTitle);
            i.putExtra("offerDescription",offerDescription);
            i.putExtra("companyAddress",companyAddress);
            i.putExtra("coordinates", coordinates);
            startActivity(i);
        }
    }


    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String mAddressOutput = resultData.getString("subAdminArea");
            city = resultData.getString("adminArea");

            if (resultCode == Constants.SUCCESS_RESULT) {
                locationText = (TextView) findViewById(R.id.locationText);
                if(mAddressOutput != null)
                {
                    locationText.setText(mAddressOutput);
                    Toast.makeText(ListActivity.this,"Current City: " + city,Toast.LENGTH_LONG).show();
                }
                else
                {
                    locationText.setText("Unable to Find");
                }

            }
            else
            {
                // Display the error message if finding the address was not successful
                Toast.makeText(ListActivity.this,mAddressOutput,Toast.LENGTH_LONG).show();
            }

            progressDialog.dismiss();
        }
    }


    class TalkToServerTask extends AsyncTask<Void,Void,Boolean> {
        // class to be used to talk to the simple socket server we used earlier

        @Override
        protected void onPreExecute() {
            createProgressDialog();
            offers = new ArrayList<Offer>();
            Log.i("DEV", "Async Task onPreExecute now.");
        }


        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Socket socket = new Socket(ipAdr, MainActivity.destPort);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                Log.i("DEV", "Will start talking to server.");
                String categoryString="";
                for(int i=0; i<categoriesArray.size();i++)
                {
                    if(i!=categoriesArray.size()-1) {
                        categoryString += categoriesArray.get(i)+"-";
                    }
                    else{
                        categoryString += categoriesArray.get(i);
                    }
                }
                String fromServer;
                String toServer = currentLatitude + ";" + currentLongitude + ";" + city + ";" + categoryString;

                out.println(toServer);
                Log.i("DEV", "Saying to Server:" + toServer);

                //while ((fromServer = in.readLine()) != null) {
                fromServer = in.readLine();
                if(fromServer != null) {
                    Log.i("DEV", "Server: " + fromServer);

                    JSONArray jArray = new JSONArray(fromServer);

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json = jArray.getJSONObject(i);
                        String title = json.getString("title");
                        String company = json.getString("company");
                        String description = json.getString("description");
                        String address = json.getString("address");
                        String lat = json.getString("latitude");
                        String lon = json.getString("longitude");
                        double latitude = (double)Double.parseDouble(lat);
                        double longitude = (double)Double.parseDouble(lon);
                        Offer offer = new Offer(title, description, company, address, latitude, longitude);

                        offers.add(offer);

                    }
                }

                //}
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("DEV", "Error in connecting with server.");
                progressDialog.dismiss();
            }

            return false;
        }

        protected void onPostExecute(Boolean bool) {

            if(bool == true)
            {
                Log.i("DEV", "Returned true");
                Toast.makeText(ListActivity.this,"Serverla cok guzel konustuk.", Toast.LENGTH_SHORT);
                OfferListAdapter adp = new OfferListAdapter(ListActivity.this, offers);
                OfferListFragment frg = new OfferListFragment();
                frg.setListAdapter(adp);

                getSupportFragmentManager().beginTransaction().add(R.id.container, frg).commit();
            }
            else
            {
                Log.i("DEV", "Returned false");
            }
            progressDialog.dismiss();
        }
    }


    class TalkToCloudServerTask extends AsyncTask<String,Void,Boolean> {
        // class to be used to talk to the Node-RED server


        @Override
        protected void onPreExecute() {
            createProgressDialog();
            offers = new ArrayList<Offer>();
            Log.i("DEV", "Async Task onPreExecute now.");
        }



        protected Boolean doInBackground(String... params) {

            String urlStr = params[0];

            try {
                String categoryString="";
                for(int i=0; i<categoriesArray.size();i++) // this loop formats the string that contains multiple selected categories
                {
                    if(i!=categoriesArray.size()-1) {
                        categoryString += categoriesArray.get(i)+"-";
                    }
                    else{
                        categoryString += categoriesArray.get(i);
                    }
                }
                String toServer = "latitude=" + currentLatitude + "&longitude=" + currentLongitude + "&city=" + city + "&category=" + categoryString;
                String fromServer;

                urlStr = urlStr + "?" + toServer;

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader( conn.getInputStream()));

                fromServer = reader.readLine();
                if(fromServer != null) {
                    Log.i("DEV", "Server: " + fromServer);

                    JSONArray jArray = new JSONArray(fromServer);

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json = jArray.getJSONObject(i);
                        String title = json.getString("title");
                        String company = json.getString("company");
                        String description = json.getString("description");
                        String address = json.getString("address");
                        String lat = json.getString("latitude");
                        String lon = json.getString("longitude");
                        double latitude = (double)Double.parseDouble(lat);
                        double longitude = (double)Double.parseDouble(lon);
                        Offer offer = new Offer(title, description, company, address, latitude, longitude);

                        offers.add(offer);

                    }
                }
                conn.disconnect();
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                Log.i("DEV", "Error in connecting with server.");
                progressDialog.dismiss();
            }


            return false;
        }

        protected void onPostExecute(Boolean bool) {

            if(bool == true)
            {
                Log.i("DEV", "Returned true");
                Toast.makeText(ListActivity.this,"Serverla cok guzel konustuk.", Toast.LENGTH_SHORT);
                OfferListAdapter adp = new OfferListAdapter(ListActivity.this, offers);
                OfferListFragment frg = new OfferListFragment();
                frg.setListAdapter(adp);

                getSupportFragmentManager().beginTransaction().add(R.id.container, frg).commit();
            }
            else
            {
                Log.i("DEV", "Returned false");
            }
            progressDialog.dismiss();
        }
    }


}
