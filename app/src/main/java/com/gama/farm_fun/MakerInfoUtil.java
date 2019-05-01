package com.gama.farm_fun;

import java.io.Serializable;

public class MakerInfoUtil implements Serializable {
    private static final long serialVersionUID = 8633299996744734593L;

    private double latitude;
    private double longitude;
    private String name;
    private String picName;
    private String description;
    public MakerInfoUtil() {}

    public MakerInfoUtil(double latitude, double longitude, String name, String picName, String description) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.picName = picName;
        this.description = description;
    }
    //toString方法
    @Override
    public String toString() {
        return "MarkerInfoUtil [latitude=" + latitude + ", longitude=" + longitude + ", name=" + name + ", imgId="
                + picName + ", description=" + description + "]";
    }
    //getter setter
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPicName() {
        return picName;
    }
    public void setPicName(String picName) {
        this.picName = picName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}