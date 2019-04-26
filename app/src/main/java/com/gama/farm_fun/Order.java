package com.gama.farm_fun;

public class Order {
    public String projectPicUrl;
    public String project;
    public String item;
    public String detail;
    public int price;
    public String status;
    public int count;
    public String type;

    public Order(String project, String item, String detail, int price, String status, int count, String type) {
        this.project = project;
        this.item = item;
        this.detail = detail;
        this.price = price;
        this.status = status;
        this.count = count;
        this.type = type;
    }

    public void setProjectPicUrl(String projectPicUrl) {
        this.projectPicUrl = projectPicUrl;
    }

}
