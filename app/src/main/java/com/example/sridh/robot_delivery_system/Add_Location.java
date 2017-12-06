package com.example.sridh.robot_delivery_system;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class Add_Location extends AppCompatActivity {
    public double pickup_latitude,pickup_longitute, drop_latitude, drop_longitude;
    private GoogleMap mMap;
    LocationDetails location = new LocationDetails();
    String pickUp ="";
    LocationDetails locationDetails = new LocationDetails();
    String drop = "";
    String url="https://maps.googleapis.com/maps/api/directions/json?";
    Boolean Create_new = false;
    private Marker currentlocationmarker;
    private Location last_location;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    public static final int PERMISSION_REQUEST_LC_CODE = 99;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_REQUEST_LC_CODE:
                if((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    //Granted
                    if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                    }
                } else {
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
                }
        }
    }

    public Boolean check_location_permission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_LC_CODE);
            } else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_LC_CODE);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__location);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            check_location_permission();
        }
        Toolbar actions = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(actions);
        Intent intent = this.getIntent();
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        PlaceAutocompleteFragment autocompleteFragment_drop = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_drop);
        if(intent.getExtras().get("Type").toString().equals("new")){
            Create_new = true;
        } else {
            Create_new = false;
            location = (LocationDetails) intent.getExtras().getSerializable("LocationDetails");
            autocompleteFragment.setText(location.getPickUp());
            autocompleteFragment_drop.setText(location.getDrop());
        }
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("", "Place: " + place.getName());
                Log.i("", "Place: " + place.toString());
                pickup_latitude = place.getLatLng().latitude;
                pickup_longitute = place.getLatLng().longitude;
                pickUp = String.valueOf(place.getAddress());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("", "An error occurred: " + status);
            }
        });

        autocompleteFragment_drop.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("", "Place: " + place.getName());
                drop_latitude = place.getLatLng().latitude;
                drop_longitude = place.getLatLng().longitude;
                drop = String.valueOf(place.getAddress());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("", "An error occurred: " + status);
            }
        });
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText load_value = (EditText) findViewById(R.id.load_value);
                if(load_value.getText().toString().trim().isEmpty()){
                    Toast.makeText(Add_Location.this,"Load value is empty",Toast.LENGTH_SHORT).show();
                } else {
                    if((pickUp.trim().isEmpty()) && (drop.trim().isEmpty())){
                        Toast.makeText(Add_Location.this,"Pick Up or drop location is empty",Toast.LENGTH_SHORT).show();
                    } else
                        {
                        if(Create_new){
                            locationDetails.setDrop(drop.trim());
                            locationDetails.setDrop_latitude(drop_latitude);
                            locationDetails.setDrop_longitude(drop_longitude);
                            locationDetails.setPickUp(pickUp.trim());
                            locationDetails.setPickUp_latitude(pickup_latitude);
                            locationDetails.setPickUp_longitude(pickup_longitute);
                            locationDetails.setQuantity(Integer.parseInt(load_value.getText().toString()));
                            url = url + "origin=" + pickup_latitude + "," + pickup_longitute + "&destination=" + drop_latitude + "," + drop_longitude + "&key=" + MainActivity.MAPS_DIRECTION;
                            Log.d("demo:",url);
                            new Get_Data(Add_Location.this, new Get_Data.AsyncResponse(){
                                @Override
                                public void processFinish(LocationDetails loca) {
                                    if(loca != null){
                                        locationDetails.setDistance(loca.getDistance());
                                        locationDetails.setTime(loca.getTime());
                                        Log.d("location",locationDetails.toString());
                                        Intent intent=new Intent();
                                        intent.putExtra(MainActivity.VALUE_KEY,locationDetails);
                                        setResult(RESULT_OK,intent);
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(Add_Location.this,"Provide valid pickup and drop location, as there is no possible routes for the given location",Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).execute(url);
                        } else {
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_location, menu);
        return true;
    }


}
