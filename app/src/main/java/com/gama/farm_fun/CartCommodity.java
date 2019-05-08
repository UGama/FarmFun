package com.gama.farm_fun;

public class CartCommodity {
    public String name;
    public String kind;
    public String picName;
    public String url;
    public int count;
    public int singlePrice;
    public String code;
    public int number;

    public CartCommodity(String name, String kind, String code, String picName, int count, int singlePrice) {
        this.name = name;
        this.kind = kind;
        this.code = code;
        this.picName = picName;
        this.count = count;
        this.singlePrice = singlePrice;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setNumber(int number) {
        this.number = number;

    }
}
