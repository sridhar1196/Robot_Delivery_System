package com.example.sridh.robot_delivery_system;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by sridh on 11/9/2017.
 */

public class LocationDetails implements Serializable{
    Double pickUp_latitude;
    Double pickUp_longitude;
    Double drop_latitude;

    @Override
    public String toString() {
        return "LocationDetails{" +
                "pickUp_latitude=" + pickUp_latitude +
                ", pickUp_longitude=" + pickUp_longitude +
                ", drop_latitude=" + drop_latitude +
                ", drop_longitude=" + drop_longitude +
                ", pickUp='" + pickUp + '\'' +
                ", drop='" + drop + '\'' +
                ", quantity=" + quantity +
                ", distance=" + distance +
                ", time=" + time +
                '}';
    }

    Double drop_longitude;
    String pickUp;
    String drop;
    int quantity;
    int distance;
    int time;

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Double getPickUp_latitude() {
        return pickUp_latitude;
    }

    public void setPickUp_latitude(Double pickUp_latitude) {
        this.pickUp_latitude = pickUp_latitude;
    }

    public Double getPickUp_longitude() {
        return pickUp_longitude;
    }

    public void setPickUp_longitude(Double pickUp_longitude) {
        this.pickUp_longitude = pickUp_longitude;
    }

    public Double getDrop_latitude() {
        return drop_latitude;
    }

    public void setDrop_latitude(Double drop_latitude) {
        this.drop_latitude = drop_latitude;
    }

    public Double getDrop_longitude() {
        return drop_longitude;
    }

    public void setDrop_longitude(Double drop_longitude) {
        this.drop_longitude = drop_longitude;
    }

    public String getPickUp() {
        return pickUp;
    }

    public void setPickUp(String pickUp) {
        this.pickUp = pickUp;
    }

    public String getDrop() {
        return drop;
    }

    public void setDrop(String drop) {
        this.drop = drop;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
