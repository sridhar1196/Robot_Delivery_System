package com.example.sridh.robot_delivery_system;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by sridh on 12/1/2017.
 */

public class Current_Route implements Comparable<Current_Route>{
    ArrayList<Output> pickup_points = new ArrayList<Output>();
    ArrayList<Output> drop_points = new ArrayList<Output>();
    ArrayList<Output> completed_points = new ArrayList<Output>();
    Output curr_point = new Output();
    int distance;
    int current_load;

    @Override
    public String toString() {
        return "Current_Route{" +
                "pickup_points=" + pickup_points +
                ", drop_points=" + drop_points +
                ", completed_points=" + completed_points +
                ", curr_point=" + curr_point +
                ", distance=" + distance +
                ", current_load=" + current_load +
                '}';
    }

    public int getCurrent_load() {
        return current_load;
    }

    public void setCurrent_load(int current_load) {
        this.current_load = current_load;
    }

    public Output getCurr_point() {
        return curr_point;
    }

    public void setCurr_point(Output curr_point) {
        this.curr_point = curr_point;
    }

    public ArrayList<Output> getCompleted_points() {
        return completed_points;
    }

    public void setCompleted_points(ArrayList<Output> completed_points) {
        this.completed_points = completed_points;
    }

    public ArrayList<Output> getPickup_points() {
        return pickup_points;
    }

    public void setPickup_points(ArrayList<Output> pickup_points) {
        this.pickup_points = pickup_points;
    }

    public ArrayList<Output> getDrop_points() {
        return drop_points;
    }

    public void setDrop_points(ArrayList<Output> drop_points) {
        this.drop_points = drop_points;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public int compareTo(@NonNull Current_Route o) {
        if ((this.distance - o.getDistance()) > 0) {
            return 1;
        } else {
            return -1;
        }
    }
}
