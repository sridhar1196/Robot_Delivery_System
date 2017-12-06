package com.example.sridh.robot_delivery_system;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sridh on 11/23/2017.
 */

public class LocationUtil {
    static public class LocationJSONParser{
        static LocationDetails parseLocation(String in) throws JSONException {
            LocationDetails locationDetails  = new LocationDetails();
            JSONObject root = new JSONObject(in);
            if(root.has("status")){
                Log.d("status:","getting");
                if(root.getString("status").equals("OK")){
                    Log.d("status ok:","getting");
                    JSONArray routesJSONArray = root.getJSONArray("routes");
                    JSONObject routesJSONObject=routesJSONArray.getJSONObject(0);
                    JSONArray legsJSONArray = routesJSONObject.getJSONArray("legs");
                    JSONObject legsJSONObject = legsJSONArray.getJSONObject(0);
                    JSONObject distance = legsJSONObject.getJSONObject("distance");
                    locationDetails.setDistance(Integer.parseInt(distance.getString("value")));
                    JSONObject duration = legsJSONObject.getJSONObject("duration");
                    locationDetails.setTime(Integer.parseInt(duration.getString("value")));
                    Log.d("location",locationDetails.toString());
                    return locationDetails;
                }
            }
            return null;
        }
        static ArrayList<String[]> parsePolyLine(String in) throws JSONException {
            LocationDetails locationDetails  = new LocationDetails();
            JSONObject root = new JSONObject(in);
            if(root.has("status")){
                Log.d("status:","getting");
                if(root.getString("status").equals("OK")){
                    Log.d("status ok:","getting");
                    JSONArray routesJSONArray = root.getJSONArray("routes");
                    JSONObject routesJSONObject=routesJSONArray.getJSONObject(0);
                    JSONArray legsJSONArray = routesJSONObject.getJSONArray("legs");
                    ArrayList<String[]> PolylinesString = new ArrayList<String[]>();
                    for(int j =0;j<legsJSONArray.length();j++){
                        Log.d("JSONArray",legsJSONArray.toString());
                        JSONObject legsJSONObject = legsJSONArray.getJSONObject(j);
                        JSONArray stepsJSONArray = legsJSONObject.getJSONArray("steps");
                        String[] stringsArray = new String[stepsJSONArray.length()];
                        for(int i=0;i<stepsJSONArray.length();i++ ){
                            JSONObject stepsJSONObject = stepsJSONArray.getJSONObject(i);
                            JSONObject polylineJSONObject = stepsJSONObject.getJSONObject("polyline");
                            String string = new String();
                            string = polylineJSONObject.getString("points");
                            stringsArray[i] = string;
                        }
                        PolylinesString.add(stringsArray);
                    }
                    return PolylinesString;
                }
            }
            return null;
        }
    }
}
