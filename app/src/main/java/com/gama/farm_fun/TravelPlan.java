package com.gama.farm_fun;

public class TravelPlan {

    public int day;
    public String name;
    public String detail;
    public String locateDescribe;
    public String picName;
    public String url;
    public String type;

    public TravelPlan(int day, String name, String locateDescribe, String picName) {
        this.day = day;
        this.name = name;
        this.locateDescribe = locateDescribe;
        this.picName = picName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
