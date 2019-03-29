package com.gama.farm_fun;

import android.graphics.Bitmap;

public class Project {

    public String title;
    public String id;
    public String describe;
    public String openTime;
    public Bitmap bitmap;
    public double price;
    public String locationDescribe;
    public String longitude;
    public String latitude;

    public Project(String title, String describe, String id) {
        this.title = title;
        this.describe = describe;
        this.id = id;
    }

}
