package com.example.sridh.robot_delivery_system;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    public final static int ADD_LOCATION = 100;
    public final static int SETTINGS = 99;
    public final static int MAPS = 99;
    public final static String MAPS_DIRECTION = "AIzaSyBhc7DLBHWEGsp-NwmIefjQF8n7I88_IUU";
    RecyclerView.LayoutManager mLayoutManager;
    public static final String VALUE_KEY="value";
    String star_selected = null;
    RecyclerView.Adapter mAdapter;
    RecyclerView mRecyclerView;
    ArrayList<LocationDetails> locations = new ArrayList<LocationDetails>();
    public final static String MY_PREFS_NAME = "LOCATIONS";
    int maximum_load = 50;
    int load;
    String algorithm = "A star";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            locations = (ArrayList<LocationDetails>) savedInstanceState.getSerializable("LIST");
        }
        setContentView(R.layout.activity_main);
        Toolbar actions = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(actions);
        mRecyclerView = (RecyclerView) findViewById(R.id.locationList);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout managerq
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new LocationListAdaptor(MainActivity.this, R.layout.location_list, locations);
        mRecyclerView.setAdapter(mAdapter);
        findViewById(R.id.findRoutes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(locations != null){
                    if(locations.size() > 0){
                        if(star_selected == null){
                            Toast.makeText(MainActivity.this,"Select initial pickup location by selecting a star",Toast.LENGTH_SHORT).show();
                        } //else if(load > maximum_load){
                            //Toast.makeText(MainActivity.this,"Load must be less than maximum load",Toast.LENGTH_SHORT).show();
                         else {
                            Boolean error = false;
                            for(int x = 0;x<locations.size();x++){
                                if(locations.get(x).getQuantity() > maximum_load){
                                    error = true;
                                    x = locations.size() + 1;
                                }
                            }
                            if(error){
                                Toast.makeText(MainActivity.this,"Inidivial Load is greater then maximum  capacity",Toast.LENGTH_SHORT).show();
                            } else {
                                Intent i = new Intent(MainActivity.this,MapsActivity.class);
                                i.putExtra("MAPS",locations);
                                i.putExtra("STAR",Integer.parseInt(star_selected));
                                i.putExtra("MAXIMUM",maximum_load);
                                i.putExtra("ALGORITHM",algorithm);
                                startActivityForResult(i,MAPS);
                            }
                        }
                    } else {
                        Toast.makeText(MainActivity.this,"Add atleast one location",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this,"Add atleast one location",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void edit_Location(LocationDetails locationDetails){
        ArrayList<LocationDetails> locations = new ArrayList<LocationDetails>();
        locations.add(locationDetails);
        Intent intent = new Intent(MainActivity.this,Add_Location.class);
        intent.putExtra("Type","new");
        intent.putExtra("LocationDetails",locations);
        startActivityForResult(intent,ADD_LOCATION);
    }

    public void change_Location(int select){
        star_selected = String.valueOf(select);
        //mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("LIST",locations);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.addLocation:
            //add the function to perform here
            Intent intent = new Intent(MainActivity.this,Add_Location.class);
            intent.putExtra("Type","new");
            startActivityForResult(intent,ADD_LOCATION);
            return(true);
        case R.id.settings:
            Intent i = new Intent(MainActivity.this,Settings.class);
            startActivityForResult(i,SETTINGS);
            return(true);
        case R.id.exit:
            //add the function to perform here
            finish();
        }
        return(super.onOptionsItemSelected(item));
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SETTINGS:
                if(resultCode == RESULT_OK){
                    maximum_load = (int) data.getExtras().get("MAXIMUM");
                    algorithm = (String) data.getExtras().get("ALGORITHM");
                }
                break;
            case ADD_LOCATION:
                if(resultCode == RESULT_OK){
                    LocationDetails loca = new LocationDetails();
                    loca = (LocationDetails) data.getExtras().getSerializable(VALUE_KEY);
                    locations.add(loca);
                    load = load + loca.getQuantity();
                    if(star_selected == null){
                        star_selected = "0";
                    }
                    mAdapter.notifyDataSetChanged();
                }
                break;
        }
    }
}
