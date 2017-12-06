package com.example.sridh.robot_delivery_system;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import com.google.maps.android.PolyUtil;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener
        , LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    private Marker currentlocationmarker;
    private Location last_location;
    private GoogleApiClient client;
    int count;
    PriorityQueue<Current_Route> Incomplete = new PriorityQueue<Current_Route>();
    private LocationRequest locationRequest;
    private AppCompatDelegate delegate;
    ArrayList<LocationDetails> locationDetails = new ArrayList<LocationDetails>();
    ArrayList<Output> Completed = new ArrayList<Output>();
    Output current_point = new Output();
    int star_selected;
    int[] colors = {Color.RED,Color.BLUE,Color.GREEN};
    int maximum_load;
    String url="https://maps.googleapis.com/maps/api/directions/json?";
    int count_dis = 0;
    String algorithm;
    HashMap<Output,HashMap<Output,Integer>> point_distance = new HashMap<Output,HashMap<Output, Integer>>();
    ArrayList<Current_Route> current_routes = new ArrayList<Current_Route>();

    public static final int PERMISSION_REQUEST_LC_CODE = 99;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_REQUEST_LC_CODE:
                if((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    //Granted
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if(client == null){
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = this.getIntent();
        locationDetails = (ArrayList<LocationDetails>) intent.getExtras().getSerializable("MAPS");
        star_selected = intent.getExtras().getInt("STAR");
        maximum_load = intent.getExtras().getInt("MAXIMUM");
        algorithm = intent.getExtras().getString("ALGORITHM");
        //setContentView(R.layout.activity_maps);
        Log.d("MAP ACTIVITY","ON CREATE");
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            check_location_permission();
        }
        count = 0;
        for(int a = 0; a< locationDetails.size();a++){
            Output pick_point = new Output();
            Output drop_point = new Output();
            pick_point.setLatitude(locationDetails.get(a).getPickUp_latitude());
            pick_point.setLongitude(locationDetails.get(a).getPickUp_longitude());
            Cal_distance(pick_point.getLatitude(),pick_point.getLongitude(),0,true,locationDetails.get(a).getDrop_latitude(),locationDetails.get(a).getDrop_longitude(),false,locationDetails.get(a).getQuantity());
            count = count + 1;
            drop_point.setLatitude(locationDetails.get(a).getDrop_latitude());
            drop_point.setLongitude(locationDetails.get(a).getDrop_longitude());
            for(int b = 0;b<locationDetails.size();b++){
                if( a != b){
                    Cal_distance(pick_point.getLatitude(),pick_point.getLongitude(),0,true,locationDetails.get(b).getDrop_latitude(),locationDetails.get(b).getDrop_longitude(),false, locationDetails.get(b).getQuantity());
                    count = count + 1;
                    Cal_distance(pick_point.getLatitude(),pick_point.getLongitude(),b,true,locationDetails.get(b).getPickUp_latitude(),locationDetails.get(b).getPickUp_longitude(),true, locationDetails.get(b).getQuantity());
                    count = count + 1;
                    Cal_distance(drop_point.getLatitude(),drop_point.getLongitude(),0,false,locationDetails.get(b).getDrop_latitude(),locationDetails.get(b).getDrop_longitude(),false, locationDetails.get(b).getQuantity());
                    count = count + 1;
                    Cal_distance(drop_point.getLatitude(),drop_point.getLongitude(),b,false,locationDetails.get(b).getPickUp_latitude(),locationDetails.get(b).getPickUp_longitude(),true, locationDetails.get(b).getQuantity());
                    count = count + 1;
                }
            }
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMarkerDragListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationDetails.get(star_selected).getPickUp_latitude(), locationDetails.get(star_selected).getPickUp_longitude()), 14.0f));
        for(int i =0;i<Completed.size();i++){
            if(Completed.get(i).getPickup()){
                Log.d("Marker Option1",Completed.get(i).toString());
                LatLng latLng = new LatLng(Completed.get(i).getLatitude(),Completed.get(i).getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pickup_truck));
                Log.d("Marker Option2",markerOptions.toString());
                mMap.addMarker(markerOptions);
            } else {
                Log.d("Marker Option3",Completed.get(i).toString());
                LatLng latLng = new LatLng(Completed.get(i).getLatitude(),Completed.get(i).getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.destination));
                Log.d("Marker Option4",markerOptions.toString());
                mMap.addMarker(markerOptions);
            }
        }

//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        last_location = location;
        if(currentlocationmarker != null){
            currentlocationmarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.draggable(true);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currentlocationmarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        if(client != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);

        }

    }

    public Boolean check_location_permission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_LC_CODE);
            } else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_LC_CODE);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.setDraggable(true);
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Toast.makeText(getBaseContext(),"Latitude["+ marker.getPosition().latitude + "], Longitude[" + marker.getPosition().longitude + "]",Toast.LENGTH_SHORT).show();
    }

    public void Cal_distance(final Double pickup_latitude, final Double pickup_longitute, final int a, final boolean b1, final Double drop_latitude, final Double drop_longitude, final Boolean b, final int quantity){
        String url1 = url + "origin=" + pickup_latitude + "," + pickup_longitute + "&destination=" + drop_latitude + "," + drop_longitude + "&key=" + MainActivity.MAPS_DIRECTION;
        Log.d("Output URL:",url1);
        new Get_Data(MapsActivity.this, new Get_Data.AsyncResponse(){
            @Override
            public void processFinish(LocationDetails loca) {
                count_dis = count_dis + 1;
                if(loca != null){
                    Output pickup_Output = new Output();
                    Output drop_Output = new Output();
                    pickup_Output.setLongitude(pickup_longitute);
                    pickup_Output.setLatitude(pickup_latitude);
                    pickup_Output.setPickup(b1);
                    drop_Output.setLatitude(drop_latitude);
                    drop_Output.setLongitude(drop_longitude);
                    drop_Output.setPickup(b);
                    if(b){
                        drop_Output.setDistance(a);
                    }
                    drop_Output.setLoad(quantity);
                    //drop_Output.setDistance(loca.getDistance());


                    HashMap<Output,Integer> value = new HashMap<Output,Integer>();
                    if(point_distance.containsKey(pickup_Output)){
                        value = point_distance.get(pickup_Output);
                        point_distance.remove(pickup_Output);
                        Log.d("inside contains",pickup_Output.toString());
                    }
                    value.put(drop_Output,loca.getDistance());
                    point_distance.put(pickup_Output,value);
                    Log.d("Pick up",pickup_Output.toString());
                    Log.d("drop Output",drop_Output.toString());
                    Log.d("Point Distance size",String.valueOf(point_distance.size()));
                    Log.d("Point Distance in size",String.valueOf(point_distance.get(pickup_Output).size()));
                    Log.d("Location Distance size",String.valueOf(locationDetails.size()));
                    if(count_dis == count){
                        check_goalstate();
                    }
//                    Output output = new Output();
//                    output.setPickup(b);
//                    output.setLongitude(drop_longitude);
//                    output.setLatitude(drop_latitude);
//                    output.setDistance(loca.getDistance());
//                    Log.d("Output:",output.toString());
//                    Incomplete.add(output);
//                    if(count == (pickup.size()+ drop.size())) {
//                        check_goalstate();
//                    }
                }
                else {
                    Toast.makeText(MapsActivity.this,"Provide valid pickup and drop location, as there is no possible routes for the given location",Toast.LENGTH_LONG).show();
                }
            }
        }).execute(url1);

    }
    public void check_goalstate(){
        ArrayList<Output> pickup = new ArrayList<Output>();
        ArrayList<Output> drop = new ArrayList<Output>();
        ArrayList<Output> Curr_Route = new ArrayList<>();
        for(int i = 0; i< locationDetails.size();i++){
            if(i != star_selected){
                Output Output = new Output();
                Output.setLatitude(locationDetails.get(i).getPickUp_latitude());
                Output.setLongitude(locationDetails.get(i).getPickUp_longitude());
                Output.setPickup(true);
                Output.setLoad(locationDetails.get(i).getQuantity());
                Output.setDistance(i);
                pickup.add(Output);
            }
        }
        Output Output = new Output();
        Output.setLatitude(locationDetails.get(star_selected).getDrop_latitude());
        Output.setLongitude(locationDetails.get(star_selected).getDrop_longitude());
        Output.setPickup(false);
        Output.setLoad(locationDetails.get(star_selected).getQuantity());
        drop.add(Output);
        int current_load = locationDetails.get(star_selected).getQuantity();
        Boolean goalstate = false;
        current_point.setLongitude(locationDetails.get(star_selected).getPickUp_longitude());
        current_point.setLatitude(locationDetails.get(star_selected).getPickUp_latitude());
        current_point.setPickup(true);
        current_point.setDistance(star_selected);
        Log.d("Output Current",current_point.toString());
        Output output = new Output();
        output.setLatitude(current_point.getLatitude());
        output.setLongitude(current_point.getLongitude());
        output.setPickup(true);
        Curr_Route.add(output);
        int total_distance = 0;
        Log.d("Output Pickup",pickup.toString());
        Log.d("Output Drop",drop.toString());
        while ((drop.size() > 0) || (pickup.size() > 0)){
            if(current_load <= maximum_load) {
                for (int i = 0; i < pickup.size(); i++) {
                    int load;
                    Current_Route current_route = new Current_Route();
                    load = pickup.get(i).getLoad() + current_load;
                    current_route.setCurrent_load(load);
                    if(load <= maximum_load){
                        int curr_distance = 0;
                        Log.d("Algorithm", algorithm);
                        if (algorithm.equals("A star")) {
                            curr_distance = point_distance.get(current_point).get(pickup.get(i));
                            curr_distance = total_distance + curr_distance;
                        } else {
                            curr_distance = point_distance.get(current_point).get(pickup.get(i));
                        }
                        ArrayList<Output> cur_pickup = new ArrayList<Output>();
                        cur_pickup.addAll(pickup);
                        cur_pickup.remove(pickup.get(i));
                        ArrayList<Output> cur_drop = new ArrayList<Output>();
                        Output Output2 = new Output();
                        Output2.setLatitude(locationDetails.get(pickup.get(i).getDistance()).getDrop_latitude());
                        Output2.setLongitude(locationDetails.get(pickup.get(i).getDistance()).getDrop_longitude());
                        Output2.setPickup(false);
                        Output2.setLoad(locationDetails.get(pickup.get(i).getDistance()).getQuantity());
                        cur_drop.addAll(drop);
                        cur_drop.add(Output2);
                        current_route.setDrop_points(cur_drop);
                        current_route.setPickup_points(cur_pickup);
                        ArrayList<Output> curr_completed = new ArrayList<Output>();
                        curr_completed.addAll(Curr_Route);
                        Output output1 = new Output();
                        output1.setPickup(true);
                        output1.setLongitude(pickup.get(i).getLongitude());
                        output1.setLatitude(pickup.get(i).getLatitude());
                        curr_completed.add(output1);
                        current_route.setCompleted_points(curr_completed);
                        current_route.setDistance(curr_distance);
                        output1.setLongitude(pickup.get(i).getLongitude());
                        output1.setLatitude(pickup.get(i).getLatitude());
                        output1.setPickup(true);
                        current_route.setCurr_point(output1);
                        Incomplete.add(current_route);
                    }
                }
            }

            for(int i = 0;i<drop.size();i++){
                Current_Route current_route = new Current_Route();
                int curr_distance =  point_distance.get(current_point).get(drop.get(i));
                curr_distance = total_distance + curr_distance;
                ArrayList<Output> cur_pickup = new ArrayList<Output>();
                ArrayList<Output> cur_drop = new ArrayList<Output>();
                cur_drop.addAll(drop);
                cur_drop.remove(drop.get(i));
                cur_pickup.addAll(pickup);
                current_route.setDrop_points(cur_drop);
                current_route.setPickup_points(cur_pickup);
                ArrayList<Output> curr_completed = new ArrayList<Output>();
                curr_completed.addAll(Curr_Route);
                Output output1 = new Output();
                output1.setPickup(false);
                output1.setLongitude(drop.get(i).getLongitude());
                output1.setLatitude(drop.get(i).getLatitude());
                curr_completed.add(output1);
                current_route.setCompleted_points(curr_completed);
                current_route.setDistance(curr_distance);
                output1.setLongitude(drop.get(i).getLongitude());
                output1.setLatitude(drop.get(i).getLatitude());
                output1.setPickup(false);
                current_route.setCurr_point(output1);
                int load;
                load = current_load - drop.get(i).getLoad();
                current_route.setCurrent_load(load);
                Incomplete.add(current_route);
            }
            if(Incomplete != null){
                Current_Route current_route = new Current_Route();
                current_route = Incomplete.poll();
                Log.d("Current Route",current_route.toString());
                pickup = current_route.getPickup_points();
                drop = current_route.getDrop_points();
                Curr_Route.removeAll(Curr_Route);
                Curr_Route.addAll(current_route.getCompleted_points());
                total_distance = current_route.getDistance();
                current_load = current_route.getCurrent_load();
                current_point.setLatitude(current_route.getCurr_point().getLatitude());
                current_point.setLongitude(current_route.getCurr_point().getLongitude());
                current_point.setPickup(current_route.getCurr_point().getPickup());
                Log.d("Output Pickup",pickup.toString());
                Log.d("Output Drop",drop.toString());
                Log.d("Total Distance",String.valueOf(total_distance));
                Log.d("Current Route",Curr_Route.toString()+"Actual:"+current_route.getCompleted_points());
                Log.d("Current Point",current_point.toString());
            }

        }
        Completed = Curr_Route;
        Log.d("Output Final",Completed.toString());
        String wayOutput = "";
        for(int i =0;i<Completed.size();i++){
            if(i == 1){
                wayOutput = "&waypoints=";
                wayOutput = wayOutput + Completed.get(i).getLatitude() + "," + Completed.get(i).getLongitude();
            } else if((i>0) && (i<Completed.size() - 1))  {
                wayOutput = wayOutput + "|" + Completed.get(i).getLatitude() + "," + Completed.get(i).getLongitude();
            }
            Log.d("Completed",Completed.get(i).toString());
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        String url1 = url + "origin=" + Completed.get(0).getLatitude() + "," + Completed.get(0).getLongitude() + "&destination=" + Completed.get(Completed.size() - 1).getLatitude() + "," + Completed.get(Completed.size() - 1).getLongitude() + wayOutput + "&key=" + MainActivity.MAPS_DIRECTION;
        Log.d("Output URL:",url1);
        new Get_data_polyline(MapsActivity.this, new Get_data_polyline.AsyncResponse() {
            @Override
            public void processFinish(ArrayList<String[]> strings) {
                for(int i =0;i<strings.size();i++){
                    String[] string = new String[strings.get(i).length];
                    string = strings.get(i);
                    for(int j =0;j<string.length;j++){
                        PolylineOptions options = new PolylineOptions();
                        options.color(colors[i%3]);
                        options.width(10);
                        options.addAll(PolyUtil.decode(string[j]));
                        mMap.addPolyline(options);
                    }
                }

            }
        }).execute(url1);
//        count = 0;
//        for(int j =0;j<pickup.size();j++){
//            Cal_distance(current_point.getLatitude(),current_point.getLongitude(),pickup.get(j).getLatitude(),pickup.get(j).getLongitude(),true);
//        }
//        for(int j =0;j<drop.size();j++){
//            Cal_distance(current_point.getLatitude(),current_point.getLongitude(),drop.get(j).getLatitude(),drop.get(j).getLongitude(),false);
//        }

//        Output output = new Output();
//        output = Incomplete.poll();
//        Incomplete.clear();
//        Log.d("Output1:",output.toString());
//        current_point.setLatitude(output.getLatitude());
//        current_point.setLongitude(output.getLongitude());
//        Completed.add(output);
//        Log.d("Output Completed:",Completed.toString());
//        if(output.getPickup()){
//            for(int i = 0; i<pickup.size();i++){
//                if((output.getLatitude() == pickup.get(i).getLatitude()) && (output.getLongitude() == pickup.get(i).getLongitude())){
//                    pickup.remove(i);
//                    i = pickup.size() + 1;
//                    for(int j = 0; j<locationDetails.size();j++){
//                        if((output.getLatitude() == locationDetails.get(j).getPickUp_latitude()) && (output.getLongitude() == locationDetails.get(j).getPickUp_longitude())){
//                            Output Output = new Output();
//                            Output.setLatitude(locationDetails.get(j).getDrop_latitude());
//                            Output.setLongitude(locationDetails.get(j).getDrop_longitude());
//                            drop.add(Output);
//                            Log.d("Output Drop add",Output.toString());
//                            j = locationDetails.size() + 1;
//                        }
//                    }
//                }
//            }
//        } else {
//            for(int i = 0; i<drop.size();i++){
//                if((output.getLatitude() == drop.get(i).getLatitude()) && (output.getLongitude() == drop.get(i).getLongitude())){
//                    drop.remove(i);
//                    i = drop.size() + 1;
//                }
//            }
//        }
//        if(Completed.size() == (locationDetails.size()*2)){
//            Log.d("Output Final",Completed.toString());
//            String wayOutput = "";
//            for(int i =0;i<Completed.size();i++){
//                if(i == 1){
//                    wayOutput = "&wayOutput=";
//                    wayOutput = wayOutput + Completed.get(i).getLatitude() + "," + Completed.get(i).getLongitude();
//                } else if((i>0) && (i<Completed.size() - 1))  {
//                    wayOutput = wayOutput + "|" + Completed.get(i).getLatitude() + "," + Completed.get(i).getLongitude();
//                }
//                Log.d("Completed",Completed.get(i).toString());
//            }
//            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                    .findFragmentById(R.id.map);
//            mapFragment.getMapAsync(this);
//            String url1 = url + "origin=" + Completed.get(0).getLatitude() + "," + Completed.get(0).getLongitude() + "&destination=" + Completed.get(Completed.size() - 1).getLatitude() + "," + Completed.get(Completed.size() - 1).getLongitude() + wayOutput + "&key=" + MainActivity.MAPS_DIRECTION;
//            Log.d("Output URL:",url1);
//            new Get_data_polyline(MapsActivity.this, new Get_data_polyline.AsyncResponse() {
//                @Override
//                public void processFinish(ArrayList<String[]> strings) {
//                    for(int i =0;i<strings.size();i++){
//                        String[] string = new String[strings.get(i).length];
//                        string = strings.get(i);
//                        for(int j =0;j<string.length;j++){
//                            PolylineOptions options = new PolylineOptions();
//                            options.color(Color.RED);
//                            options.width(10);
//                            options.addAll(PolyUtil.decode(string[j]));
//                            mMap.addPolyline(options);
//                        }
//                    }
//
//                }
//            }).execute(url1);
//        } else {
//            Output output1 = new Output();
//            output1.setLatitude(current_point.getLatitude());
//            output1.setLongitude(current_point.getLongitude());
//            output1.setPickup(true);
//            count = 0;
//            Log.d("Output Pickup",pickup.toString());
//            Log.d("Output Drop",drop.toString());
//            for(int j = 0; j < pickup.size();j++){
//                Cal_distance(current_point.getLatitude(),current_point.getLongitude(),pickup.get(j).getLatitude(),pickup.get(j).getLongitude(),true);
//            }
//            for(int j =0;j < drop.size();j++){
//                Cal_distance(current_point.getLatitude(),current_point.getLongitude(),drop.get(j).getLatitude(),drop.get(j).getLongitude(),false);
//            }
//        }

    }
}
