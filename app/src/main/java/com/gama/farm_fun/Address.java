package com.gama.farm_fun;

public class Address {
    public String address;
    public String phone;
    public String name;
    public String id;

    public Address(String address, String phone, String name) {
        this.address = address;
        this.name = name;
        this.phone = phone;
    }

    public void setId(String id) {
        this.id = id;
    }
}
