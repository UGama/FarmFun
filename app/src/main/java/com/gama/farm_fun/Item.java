package com.gama.farm_fun;

public class Item {
    public String picUrl;
    public String name;
    public String detail;
    public int count;
    public int price;

    public Item(String picUrl, String name, String detail, int count, int price) {
        this.picUrl = picUrl;
        this.name = name;
        this.detail = detail;
        this.count = count;
        this.price = price;
    }
}
