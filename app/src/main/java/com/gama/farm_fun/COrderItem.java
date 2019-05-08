package com.gama.farm_fun;

public class COrderItem {
    public String name;
    public String kind;
    public String picName;
    public int count;
    public String url;

    public COrderItem(String name, String kind, String picName, int count) {
        this.name = name;
        this.kind = kind;
        this.picName = picName;
        this.count = count;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
