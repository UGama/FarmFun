package com.gama.farm_fun;

public class COrder {
    public String id;
    public String[] names;
    public String[] codes;
    public String[] kinds;
    public int[] counts;
    public int price;
    public String address;
    public String name;
    public String phone;
    public boolean comment;
    public String[] urls;
    public String status;
    public String[] picName;
    public String type;
    public String codesString;
    public String nameString;

    public COrder(String id, String[] kinds, String[] names, String[] codes, int[] counts, int price, String address, String name, String phone, boolean comment, String status, String[] picName) {
        this.id = id;
        this.names = names;
        this.codes = codes;
        this.counts = counts;
        this.price = price;
        this.address = address;
        this.name = name;
        this.phone = phone;
        this.picName = picName;
        this.status = status;
        this.kinds = kinds;
        this.comment = comment;
    }

    public void setUrls(String[] urls) {
        this.urls = urls;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCodesString(String codesString) {
        this.codesString = codesString;
    }

    public void setNameString(String nameString) {
        this.nameString = nameString;
    }
}
