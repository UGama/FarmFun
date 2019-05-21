package com.gama.farm_fun;

public class RecommendProject {
    public String name;
    public String type;
    public String picName;
    public String url;

    public RecommendProject(String name, String type, String picName) {
        this.name = name;
        this.type = type;
        this.picName = picName;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
