package cs310.brkdncr.liveoffer_deneme2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by brkdn on 28/04/2016.
 */
public class SelectionActivity extends AppCompatActivity{

    // #1edc37 green
    // #dc3e1e orange



    //public String categories = null;
    //public Set<string> categories = new Set<string>();
    //HashMap<String, Boolean> selections = new HashMap<String, Boolean>();
    //boolean[] selections = new boolean[4];
    boolean food = false; // to check if the corresponding categories have been selected already
    boolean music = false;
    boolean clothing = false;
    boolean beauty = false;
    Button btn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
    }

    public void addCategory(View view)
    {
        String category = null;

        switch(view.getId())
        {
            case R.id.foodButton:
                if(food != true) {
                    food = true;
                    btn = (Button)findViewById(R.id.foodButton);
                    btn.setBackgroundColor(Color.parseColor("#1edc37"));
                }
                else
                {
                    btn = (Button)findViewById(R.id.foodButton);
                    btn.setBackgroundColor(Color.parseColor("#dc3e1e"));
                    food = false;
                }
                break;
            case R.id.musicButton:
                if(music != true) {
                    music = true;
                    btn = (Button)findViewById(R.id.musicButton);
                    btn.setBackgroundColor(Color.parseColor("#1edc37"));
                }
                else
                {
                    music = false;
                    btn = (Button)findViewById(R.id.musicButton);
                    btn.setBackgroundColor(Color.parseColor("#dc3e1e"));
                }
                break;
            case R.id.clothingButton:
                if(clothing != true) {
                    clothing = true;
                    btn = (Button)findViewById(R.id.clothingButton);
                    btn.setBackgroundColor(Color.parseColor("#1edc37"));
                }
                else
                {
                    clothing = false;
                    btn = (Button)findViewById(R.id.clothingButton);
                    btn.setBackgroundColor(Color.parseColor("#dc3e1e"));
                }
                break;
            case R.id.beautyButton:
                if(beauty != true) {
                    beauty = true;
                    btn = (Button)findViewById(R.id.beautyButton);
                    btn.setBackgroundColor(Color.parseColor("#1edc37"));
                }
                else
                {
                    beauty = false;
                    btn = (Button)findViewById(R.id.beautyButton);
                    btn.setBackgroundColor(Color.parseColor("#dc3e1e"));
                }
                break;
            default:
                throw new RuntimeException("Unknown button ID");
        }

    }

    public void scanDeals(View view)
    {
        if(food || music || clothing || beauty)
        {
            // get the selected categories stored in an array
            //Set<String> keySet = selections.keySet();
            ArrayList<String> categoriesArray = new ArrayList<String>();
            if(food)
            {
                categoriesArray.add("Food");
            }
            if(music)
            {
                categoriesArray.add("Music");
            }
            if(clothing)
            {
                categoriesArray.add("Clothing");
            }
            if(beauty)
            {
                categoriesArray.add("Beauty");
            }

            Intent i = new Intent(SelectionActivity.this, ListActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("CategoriesArray", categoriesArray);
            startActivity(i);
        }
        else
        {
            Toast.makeText(this,"Please select at least one category",Toast.LENGTH_SHORT).show();
        }
    }

}
