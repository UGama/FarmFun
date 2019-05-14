package com.gama.farm_fun;

public class ProjectRankStatistics {

    public String code;
    public int rank;
    public int count;

    public float average;

    public int sales;

    public ProjectRankStatistics(String code) {
        this.code = code;
    }

    public ProjectRankStatistics(String code, int rank) {
        this.code = code;
        this.rank = rank;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void addRank(int rank) {
        this.rank += rank;
    }

    public void addCount() {
        this.count++;
    }

    public void setAverage(float rank, float count) {
        this.average = rank / count;
    }

    public void initAverage() {
        this.average = 0;
    }
}
