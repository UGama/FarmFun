package com.gama.farm_fun;

public class Commodity {

    public String name;
    public String describe;
    public String code;
    public int lowestPrice;
    public String url;

    public Commodity(String name, String describe, String code, int lowestPrice) {
        this.name = name;
        this.describe = describe;
        this.code = code;
        this.lowestPrice = lowestPrice;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
