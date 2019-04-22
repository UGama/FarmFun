package com.gama.farm_fun;

public class Month {
    public int firstDayWeek;
    public String month;
    public int days;

    public Month(int firstDayWeek, String month, int days) {
        this.firstDayWeek = firstDayWeek;
        this.month = month;
        this.days = days;
    }

    public void setDays(int days) {
        this.days = days;
    }
}
