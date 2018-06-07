package cs310.brkdncr.liveoffer_deneme2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText ipAdress;
    public static String ipAdr;
    public final static int destPort = 8882;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipAdress = (EditText) findViewById(R.id.ipAddress);
    }

    public void scanDeals(View view) {

        try {
            //ipAdr = ipAdress.getText().toString();
            ipAdr = "10.50.157.215";
            Intent i = new Intent(MainActivity.this, SelectionActivity.class);
            // i.putExtra("ipAdress", ipAdr);
            startActivity(i);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void about(View view) {
        setContentView(R.layout.aboutbtnclicked);
        Button aboutBtn = (Button)findViewById(R.id.backhomebtn);

        if (aboutBtn != null) {
            aboutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setContentView(R.layout.activity_main);
                }

            });
        }
    }
}
