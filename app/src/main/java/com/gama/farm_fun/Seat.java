package com.gama.farm_fun;

public class Seat {
    public String seatType;
    public String meal;
    public int remain;
    public String picName;
    public String describe;

    public Seat(String seatType, String meal, int remain, String picName, String describe) {
        this.seatType = seatType;
        this.meal = meal;
        this.remain = remain;
        this.picName = picName;
        this.describe = describe;
    }

    public Seat(String picName, String seatType, String describe, String meal) {
        this.picName = picName;
        this.seatType = seatType;
        this.describe = describe;
        this.meal = meal;
    }

    public void setRemain(int remain) {
        this.remain = remain;
    }
}
