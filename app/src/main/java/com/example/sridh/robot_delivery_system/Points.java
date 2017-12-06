package com.example.sridh.robot_delivery_system;

/**
 * Created by sridh on 11/25/2017.
 */

public class Points {
    Double latitude;
    Double langitude;

    @Override
    public String toString() {
        return "Points{" +
                "latitude=" + latitude +
                ", langitude=" + langitude +
                '}';
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return langitude;
    }

    public void setLangitude(Double langitude) {
        this.langitude = langitude;
    }
}
