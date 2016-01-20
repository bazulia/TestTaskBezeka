package com.example.bezeka.testtaskbezeka.model;

import android.os.Environment;

/**
 * Created by Bezeka on 19.01.2016.
 */
public class Image {

    int id;
    String dateTime;
    String tag;
    String path;
    double lat;
    double lng;

    public Image(){

    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String tag) {
        this.path = Environment
                .getExternalStorageDirectory().toString()
                + "/"+tag+".jpg";
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }


}
