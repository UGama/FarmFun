package com.gama.farm_fun;

public class Comment {
    public String userId;
    public String userName;
    public String comment;
    public int rank;
    public String item;
    public String createdAt;

    public Comment(String userId, String comment, int rank, String item, String createdAt) {
        this.userId = userId;
        this.comment = comment;
        this.rank = rank;
        this.item = item;
        this.createdAt = createdAt;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
