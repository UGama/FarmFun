package com.gama.farm_fun;

public class TravelJournal {
    public String title;
    public String subTitle;
    public String article;
    public int number;

    public TravelJournal(int number, String title, String subTitle) {
        this.number = number;
        this.title = title;
        this.subTitle = subTitle;
    }

    public void setArticle(String article) {
        this.article = article;
    }
}
