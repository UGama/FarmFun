package com.gama.farm_fun;

public class Room {
    public String roomType;
    public String roomDescribe;
    public int price;
    public int remain;
    public String roomPicName;

    public Room(String roomType, String roomDescribe, int price, int remain, String roomPicName) {
        this.roomType = roomType;
        this.roomDescribe = roomDescribe;
        this.price = price;
        this.remain = remain;
        this.roomPicName = roomPicName;
    }
}
