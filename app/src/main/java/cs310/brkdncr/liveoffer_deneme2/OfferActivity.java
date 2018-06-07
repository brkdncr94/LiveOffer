package cs310.brkdncr.liveoffer_deneme2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

/**
 * Created by brkdn on 28/04/2016.
 */
public class OfferActivity extends AppCompatActivity {

    TextView offerTitle;
    TextView offerDescription;
    TextView companyName;
    TextView companyAddress;
    TextView companyCoordinates;

    double latitude;
    double longitude;
    String companyLabel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        offerTitle = (TextView)findViewById(R.id.offerTitle);
        offerDescription = (TextView)findViewById(R.id.offerDescription);
        companyName = (TextView)findViewById(R.id.companyName);
        companyAddress = (TextView)findViewById(R.id.companyAddress);
        companyCoordinates = (TextView)findViewById(R.id.companyCoordinates);

        Intent i = getIntent();
        offerTitle.setText(i.getStringExtra("offerTitle"));
        offerDescription.setText(i.getStringExtra("offerDescription"));
        companyName.setText(i.getStringExtra("companyName"));
        companyAddress.setText(i.getStringExtra("companyAddress"));


        companyLabel = i.getStringExtra("companyName");
        double[] coordinates = i.getDoubleArrayExtra("coordinates");
        latitude = coordinates[0];
        longitude = coordinates[1];

        companyCoordinates.setText(latitude + "," + longitude);
    }

    public void goToMap(View view)
    {
        //Uri location = Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California");
        //String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(Label+Ses12)", 59.861057,  17.645816, 59.861057, 17.645816);
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f"+companyLabel, latitude,  longitude, latitude, longitude);
        //Uri location = Uri.parse("geo:59.861057,17.645816?q=59.861057,17.645816(Label+Name)");
        Uri location = Uri.parse(uri);


// Or map point based on latitude/longitude
// Uri location = Uri.parse("geo:37.422219,-122.08364?z=14"); // z param is zoom level
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);

        PackageManager packageManager = getPackageManager();
        List activities = packageManager.queryIntentActivities(mapIntent,PackageManager.MATCH_DEFAULT_ONLY);
        //boolean isIntentSafe = activities.size() > 0;
        if(activities.size() > 0)
        {
            startActivity(mapIntent);
        }
    }
}
