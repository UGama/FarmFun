package com.gama.farm_fun;

public class Ticket {
    public String projectName;
    public int price;
    public String detail;
    public String ticketType;

    public Ticket(String projectName, String ticketType, int price) {
        this.projectName = projectName;
        this.ticketType = ticketType;
        this.price = price;
    }
}
