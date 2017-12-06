package com.example.sridh.robot_delivery_system;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sridh on 11/28/2017.
 */

public class Get_data_polyline extends AsyncTask<String, Integer, ArrayList<String[]>> {

    public AsyncResponse response;
    Context add_location;
    public Get_data_polyline(Context add_location,  AsyncResponse asyncResponse) {
        this.response = asyncResponse;
        this.add_location = add_location;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(ArrayList<String[]> locationDetails) {
        super.onPostExecute(locationDetails);
        response.processFinish(locationDetails);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }


    @Override
    protected ArrayList<String[]> doInBackground(String... strings) {
        URL url = null;
        try {
            url = new URL(strings[0]);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            int status = con.getResponseCode();
            if(status == HttpURLConnection.HTTP_OK){
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = reader.readLine();
                while (line != null){
                    sb.append(line);
                    line = reader.readLine();
                }
                return LocationUtil.LocationJSONParser.parsePolyLine(sb.toString());
            }
            else {
                //Toast.makeText(getActivity(),"Error in loading question", Toast.LENGTH_LONG).show();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public interface AsyncResponse {
        void processFinish(ArrayList<String[]> strings);
    }
}
