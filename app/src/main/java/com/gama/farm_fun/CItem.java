package com.gama.farm_fun;

public class CItem {
    public String CName;
    public String code;
    public int price;
    public String name;
    public String picName;
    public String url;

    public CItem(String CName, String code, int price, String name, String picName) {
        this.CName = CName;
        this.code = code;
        this.price = price;
        this.name = name;
        this.picName = picName;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
