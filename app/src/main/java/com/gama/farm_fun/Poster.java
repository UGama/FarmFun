package com.gama.farm_fun;

/**
 * Created by Gama on 11/30/17.
 */

public class Poster {
    private int SourceId;
    public String url;
    public String title;
    public Poster(int SourceId) {
        this.SourceId = SourceId;
    }

    public int getSourceId() {
        return SourceId;
    }

    public void setSourceId(int sourceId) {
        SourceId = sourceId;
    }

    public Poster(String url, String title) {
        this.url = url;
        this.title = title;
    }
}
