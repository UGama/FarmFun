package com.gama.farm_fun;

public class Ticket {
    public String projectName;
    public int price;
    public String detail;
    public String ticketType;
    public int sales;

    public Ticket(String projectName, String ticketType, int price, int sales) {
        this.projectName = projectName;
        this.ticketType = ticketType;
        this.price = price;
        this.sales = sales;
    }
}
