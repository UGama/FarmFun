package com.gama.farm_fun;

public class TravelJournal {
    public String title;
    public String subTitle;
    public String article;
    public int number;
    public String firstPic;
    public String url;

    public TravelJournal(int number, String title, String subTitle,String firstPic) {
        this.number = number;
        this.title = title;
        this.subTitle = subTitle;
        this.firstPic = firstPic;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
