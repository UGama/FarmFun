package com.gama.farm_fun;

public class Item {
    public String picUrl;
    public String name;
    public String detail;
    public int count;
    public int price;

    public Item(String name, String picUrl, String detail, int count, int price) {
        this.name = name;
        this.picUrl = picUrl;
        this.detail = detail;
        this.count = count;
        this.price = price;
    }
}
