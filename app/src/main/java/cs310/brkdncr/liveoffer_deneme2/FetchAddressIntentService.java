package cs310.brkdncr.liveoffer_deneme2;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by brkdn on 09/05/2016.
 */
public class FetchAddressIntentService extends IntentService {

    protected ResultReceiver mReceiver;

    public FetchAddressIntentService()
    {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        //Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        String errorMessage = "";

        double latitude = intent.getDoubleExtra("Latitude", 0);
        double longitude = intent.getDoubleExtra("Longitude", 0);


        List<Address> addresses = null;

        try {
            // In this sample, get just a single address.
            //addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        }
        catch (Exception e)
        {
            errorMessage = e.getMessage();
            Log.i("DEV", e.getMessage());
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0)
        {
            if (errorMessage.isEmpty()) {
                errorMessage = "ERROR: No addresses were found.";
            }
            deliverResultToReceiverError(Constants.FAILURE_RESULT, errorMessage);
        }
        else
        {
            Address address = addresses.get(0);
            String subAdminArea = address.getSubAdminArea();
            String adminArea = address.getAdminArea();
            deliverResultToReceiver(Constants.SUCCESS_RESULT, subAdminArea, adminArea);
        }
    }

    private void deliverResultToReceiver(int resultCode, String subAdminArea, String adminArea) {
        Bundle bundle = new Bundle();
        bundle.putString("subAdminArea", subAdminArea);
        bundle.putString("adminArea", adminArea);
        mReceiver.send(resultCode, bundle);
    }

    private void deliverResultToReceiverError(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }
}
