package com.gama.farm_fun;

public class Project {
    public String name;
    public String locateDescribe;
    public String url;
    public String picName;

    public Project(String name, String locateDescribe, String picName) {
        this.name = name;
        this.locateDescribe = locateDescribe;
        this.picName = picName;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
