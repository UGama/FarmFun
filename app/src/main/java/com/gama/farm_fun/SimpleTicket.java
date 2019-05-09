package com.gama.farm_fun;

public class SimpleTicket {

    public String name;
    public int count;
    public int totalPrice;
    public String type;

    public SimpleTicket(String name, int count, int totalPrice) {
        this.name = name;
        this.count = count;
        this.totalPrice = totalPrice;
    }

    public void setType(String type) {
        this.type = type;
    }
}
