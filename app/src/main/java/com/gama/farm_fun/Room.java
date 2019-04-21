package com.gama.farm_fun;

public class Room {
    public String roomType;
    public String roomDescribe;
    public String bed;
    public int price;
    public int remain;
    public String roomPicName;

    public Room(String roomType, String roomDescribe, String bed, int price, int remain, String roomPicName) {
        this.roomType = roomType;
        this.roomDescribe = roomDescribe;
        this.bed = bed;
        this.price = price;
        this.remain = remain;
        this.roomPicName = roomPicName;
    }

    public Room(String roomType, String roomDescribe, String bed, String roomPicName) {
        this.roomType = roomType;
        this.roomDescribe = roomDescribe;
        this.bed = bed;
        this.roomPicName = roomPicName;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setRemain(int remain) {
        this.remain = remain;
    }
}
